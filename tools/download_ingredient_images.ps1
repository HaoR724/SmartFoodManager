param(
    [int]$Width = 360
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$catalogPath = Join-Path $root "app\src\main\java\com\example\smartfood\util\FoodCatalog.java"
$outDir = Join-Path $root "app\src\main\res\drawable-nodpi"
$assetDir = Join-Path $root "app\src\main\assets"
$sourcePath = Join-Path $assetDir "ingredient_image_sources.txt"

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
New-Item -ItemType Directory -Force -Path $assetDir | Out-Null

# ASCII-only keyword list. Its order matches FoodCatalog.INGREDIENTS.
# Keeping this file ASCII avoids Windows PowerShell 5 parsing errors on non-UTF8 consoles.
$queries = @(
    "broccoli vegetable",
    "red cabbage vegetable",
    "youmai lettuce vegetable",
    "bok choy vegetable",
    "shanghai bok choy vegetable",
    "baby napa cabbage",
    "spinach leaves",
    "lettuce vegetable",
    "endive lettuce vegetable",
    "water spinach vegetable",
    "garland chrysanthemum vegetable",
    "celery vegetable",
    "garlic chives vegetable",
    "coriander leaves",
    "choy sum vegetable",
    "amaranth leaves vegetable",
    "gai lan chinese broccoli",
    "lettuce leaves",
    "cabbage vegetable",
    "romaine lettuce",
    "potato",
    "carrot",
    "daikon radish",
    "sweet potato",
    "chinese yam",
    "lotus root",
    "taro root",
    "celtuce stem",
    "bamboo shoots",
    "onion",
    "spring onion scallion",
    "ginger root",
    "garlic bulb",
    "purple sweet potato",
    "beetroot",
    "tomato",
    "cucumber",
    "eggplant",
    "green bell pepper",
    "red bell pepper",
    "bell pepper",
    "pumpkin",
    "winter melon",
    "luffa gourd",
    "bitter melon",
    "zucchini",
    "chayote",
    "okra",
    "corn cob",
    "green peas",
    "shiitake mushroom",
    "enoki mushroom",
    "king oyster mushroom",
    "oyster mushroom",
    "button mushroom",
    "shimeji mushroom",
    "white beech mushroom",
    "agrocybe aegerita mushroom",
    "wood ear mushroom",
    "white fungus tremella",
    "apple fruit",
    "banana fruit",
    "orange fruit",
    "kiwifruit",
    "strawberry fruit",
    "blueberry fruit",
    "grapes fruit",
    "pear fruit",
    "peach fruit",
    "watermelon fruit",
    "hami melon cantaloupe",
    "pomelo fruit",
    "lemon fruit",
    "dragon fruit pitaya",
    "mango fruit",
    "chicken breast meat",
    "chicken leg meat",
    "chicken wings raw",
    "beef meat",
    "beef brisket",
    "pork tenderloin",
    "pork belly",
    "pork ribs",
    "lamb meat",
    "duck meat",
    "ham food",
    "luncheon meat",
    "bacon",
    "beef balls",
    "chicken meatballs",
    "salmon fillet",
    "cod fish",
    "hairtail fish",
    "sea bass fish",
    "sole fish fillet",
    "shrimp peeled",
    "prawn shrimp",
    "scallop seafood",
    "squid seafood",
    "clam seafood",
    "chicken eggs",
    "duck eggs",
    "milk bottle",
    "yogurt",
    "cheese",
    "butter",
    "tofu",
    "silken tofu",
    "firm tofu",
    "tofu skin",
    "dried tofu sticks",
    "dried tofu",
    "tofu sheets",
    "rice grains",
    "millet grain",
    "noodles",
    "spaghetti pasta",
    "oats",
    "whole wheat bread",
    "mantou steamed bun",
    "cornmeal",
    "buckwheat noodles",
    "soy sauce bottle",
    "dark soy sauce",
    "oyster sauce",
    "salt",
    "white sugar",
    "black pepper",
    "chili powder",
    "sesame oil",
    "rice vinegar",
    "cooking wine",
    "peanuts",
    "walnuts",
    "almonds",
    "cashew nuts",
    "black sesame seeds",
    "adzuki beans",
    "mung beans",
    "soybeans"
)

function Get-IngredientRows {
    $text = Get-Content -Raw -Encoding UTF8 $catalogPath
    $matches = [regex]::Matches($text, '\{"([^"]+)","([^"]+)"\}')
    $rows = New-Object System.Collections.Generic.List[object]
    $index = 1
    foreach ($m in $matches) {
        $rows.Add([pscustomobject]@{
            Index = $index
            Name = $m.Groups[1].Value
            Category = $m.Groups[2].Value
        })
        $index++
    }
    return $rows
}

function Find-CommonsImage($row) {
    $queryIndex = [Math]::Min($row.Index - 1, $queries.Count - 1)
    $query = $queries[$queryIndex]
    if ([string]::IsNullOrWhiteSpace($query)) {
        $query = "$($row.Name) food ingredient"
    }
    $encoded = [System.Uri]::EscapeDataString($query)
    $api = "https://commons.wikimedia.org/w/api.php?action=query&format=json&generator=search&gsrnamespace=6&gsrlimit=8&gsrsearch=$encoded&prop=imageinfo&iiprop=url|mime|extmetadata&iiurlwidth=$Width"
    $headers = @{ "User-Agent" = "SmartFoodCourseDesign/1.0 (local Android course project)" }
    $result = Invoke-RestMethod -Uri $api -Headers $headers -TimeoutSec 25
    if ($null -eq $result.query.pages) { return $null }

    foreach ($page in $result.query.pages.PSObject.Properties.Value) {
        if ($null -eq $page.imageinfo -or $page.imageinfo.Count -eq 0) { continue }
        $info = $page.imageinfo[0]
        if ($info.mime -notmatch '^image/(jpeg|png|webp)$') { continue }
        $url = if ($info.thumburl) { $info.thumburl } else { $info.url }
        if (-not $url) { continue }

        $author = ""
        $license = ""
        if ($info.extmetadata -and $info.extmetadata.Artist) {
            $author = ($info.extmetadata.Artist.value -replace '<[^>]+>', '')
        }
        if ($info.extmetadata -and $info.extmetadata.LicenseShortName) {
            $license = $info.extmetadata.LicenseShortName.value
        }
        return [pscustomobject]@{
            Title = $page.title
            Url = $url
            OriginalUrl = $info.url
            Mime = $info.mime
            Author = $author
            License = $license
        }
    }
    return $null
}

function Get-Extension($mime) {
    if ($mime -eq "image/png") { return ".png" }
    if ($mime -eq "image/webp") { return ".webp" }
    return ".jpg"
}

$rows = Get-IngredientRows
$sources = New-Object System.Collections.Generic.List[string]
$sources.Add("Ingredient images downloaded from Wikimedia Commons. Licenses and authors are kept for course-design attribution.")

foreach ($row in $rows) {
    $baseName = "ingredient_{0:D3}" -f $row.Index
    Write-Host "[$($row.Index)/$($rows.Count)] $($row.Name)"
    try {
        $image = Find-CommonsImage $row
        if ($null -eq $image) {
            Write-Warning "No image found for $($row.Name)"
            $sources.Add("$baseName`t$($row.Name)`tNOT_FOUND")
            continue
        }

        Get-ChildItem -LiteralPath $outDir -Filter "$baseName.*" | Remove-Item -Force
        $ext = Get-Extension $image.Mime
        $target = Join-Path $outDir "$baseName$ext"
        Invoke-WebRequest -Uri $image.Url -Headers @{ "User-Agent" = "SmartFoodCourseDesign/1.0" } -OutFile $target -TimeoutSec 35
        $sources.Add("$baseName`t$($row.Name)`t$($image.Title)`t$($image.License)`t$($image.Author)`t$($image.OriginalUrl)")
        Start-Sleep -Milliseconds 120
    } catch {
        Write-Warning "Failed $($row.Name): $($_.Exception.Message)"
        $sources.Add("$baseName`t$($row.Name)`tFAILED`t$($_.Exception.Message)")
    }
}

$sources | Set-Content -Encoding UTF8 $sourcePath
Write-Host "Done. Images: $outDir"
Write-Host "Sources: $sourcePath"
