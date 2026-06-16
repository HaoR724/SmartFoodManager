param(
    [string]$PictureDir = ".\tools\picture"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$sourceRoot = Join-Path $root ($PictureDir -replace '^\.[\\/]', '')
$outDir = Join-Path $root "app\src\main\res\drawable-nodpi"
$assetDir = Join-Path $root "app\src\main\assets"
$sourceLog = Join-Path $assetDir "project_picture_image_sources.txt"

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
New-Item -ItemType Directory -Force -Path $assetDir | Out-Null

if (-not (Test-Path -LiteralPath $sourceRoot)) {
    throw "Picture directory not found: $sourceRoot"
}

function Find-FirstImage($relativePath) {
    $dir = Join-Path $sourceRoot $relativePath
    if (-not (Test-Path -LiteralPath $dir)) {
        return $null
    }
    return Get-ChildItem -LiteralPath $dir -Recurse -File |
        Where-Object { $_.Extension -match '^\.(jpg|jpeg|png|webp)$' } |
        Sort-Object FullName |
        Select-Object -First 1
}

function Get-TargetExtension($file) {
    $ext = $file.Extension.ToLowerInvariant()
    if ($ext -eq ".jpeg") { return ".jpg" }
    if ($ext -in @(".jpg", ".png", ".webp")) { return $ext }
    return ".jpg"
}

function Copy-ToIngredient($index, $relativePath, $label, $logs) {
    $file = Find-FirstImage $relativePath
    $baseName = "ingredient_{0:D3}" -f $index
    if ($null -eq $file) {
        Write-Warning "No image found for $baseName from $relativePath"
        $logs.Add("$baseName`t$label`tNOT_FOUND`t$relativePath")
        return
    }
    Get-ChildItem -LiteralPath $outDir -Filter "$baseName.*" | Remove-Item -Force
    $target = Join-Path $outDir "$baseName$(Get-TargetExtension $file)"
    Copy-Item -LiteralPath $file.FullName -Destination $target -Force
    Write-Host "$baseName <- $relativePath"
    $logs.Add("$baseName`t$label`t$($file.FullName)")
}

$logs = New-Object System.Collections.Generic.List[string]
$logs.Add("Images imported from tools/picture into drawable-nodpi.")

# Vegetable / ingredient mappings available in tools/picture.
$map = @(
    @{ Index = 3; Path = "Vegetables\Cabbage"; Label = "leafy-cabbage" },
    @{ Index = 4; Path = "Vegetables\Cabbage"; Label = "bok-choy-fallback" },
    @{ Index = 5; Path = "Vegetables\Cabbage"; Label = "shanghai-green-fallback" },
    @{ Index = 6; Path = "Vegetables\Cabbage"; Label = "baby-cabbage-fallback" },
    @{ Index = 7; Path = "Vegetables\Cabbage"; Label = "spinach-fallback" },
    @{ Index = 8; Path = "Vegetables\Cabbage"; Label = "lettuce-fallback" },
    @{ Index = 12; Path = "Vegetables\Leek"; Label = "celery-fallback" },
    @{ Index = 13; Path = "Vegetables\Leek"; Label = "chives-fallback" },
    @{ Index = 15; Path = "Vegetables\Cabbage"; Label = "rapeseed-fallback" },
    @{ Index = 19; Path = "Vegetables\Cabbage"; Label = "cabbage" },
    @{ Index = 21; Path = "Vegetables\Potato"; Label = "potato" },
    @{ Index = 22; Path = "Vegetables\Carrots"; Label = "carrot" },
    @{ Index = 28; Path = "Vegetables\Asparagus"; Label = "celtuce-fallback" },
    @{ Index = 29; Path = "Vegetables\Asparagus"; Label = "bamboo-shoot-fallback" },
    @{ Index = 30; Path = "Vegetables\Onion"; Label = "onion" },
    @{ Index = 31; Path = "Vegetables\Leek"; Label = "scallion" },
    @{ Index = 32; Path = "Vegetables\Ginger"; Label = "ginger" },
    @{ Index = 33; Path = "Vegetables\Garlic"; Label = "garlic" },
    @{ Index = 35; Path = "Vegetables\Red-Beet"; Label = "beetroot" },
    @{ Index = 36; Path = "Vegetables\Tomato"; Label = "tomato" },
    @{ Index = 37; Path = "Vegetables\Cucumber"; Label = "cucumber" },
    @{ Index = 38; Path = "Vegetables\Aubergine"; Label = "eggplant" },
    @{ Index = 39; Path = "Vegetables\Pepper\Green-Bell-Pepper"; Label = "green-pepper" },
    @{ Index = 40; Path = "Vegetables\Pepper\Red-Bell-Pepper"; Label = "red-pepper" },
    @{ Index = 41; Path = "Vegetables\Pepper"; Label = "bell-pepper" },
    @{ Index = 46; Path = "Vegetables\Zucchini"; Label = "zucchini" },
    @{ Index = 51; Path = "Vegetables\Brown-Cap-Mushroom"; Label = "mushroom" },
    @{ Index = 52; Path = "Vegetables\Brown-Cap-Mushroom"; Label = "enoki-fallback" },
    @{ Index = 53; Path = "Vegetables\Brown-Cap-Mushroom"; Label = "king-oyster-fallback" },
    @{ Index = 54; Path = "Vegetables\Brown-Cap-Mushroom"; Label = "oyster-mushroom-fallback" },
    @{ Index = 55; Path = "Vegetables\Brown-Cap-Mushroom"; Label = "button-mushroom-fallback" },
    @{ Index = 56; Path = "Vegetables\Brown-Cap-Mushroom"; Label = "shimeji-fallback" },
    @{ Index = 61; Path = "Fruit\Apple"; Label = "apple" },
    @{ Index = 62; Path = "Fruit\Banana"; Label = "banana" },
    @{ Index = 63; Path = "Fruit\Orange"; Label = "orange" },
    @{ Index = 64; Path = "Fruit\Kiwi"; Label = "kiwi" },
    @{ Index = 68; Path = "Fruit\Pear"; Label = "pear" },
    @{ Index = 69; Path = "Fruit\Peach"; Label = "peach" },
    @{ Index = 71; Path = "Fruit\Melon"; Label = "melon" },
    @{ Index = 72; Path = "Fruit\Red-Grapefruit"; Label = "grapefruit" },
    @{ Index = 73; Path = "Fruit\Lemon"; Label = "lemon" },
    @{ Index = 74; Path = "Fruit\Passion-Fruit"; Label = "dragon-fruit-fallback" },
    @{ Index = 75; Path = "Fruit\Mango"; Label = "mango" },
    @{ Index = 103; Path = "Packages\Milk"; Label = "milk" },
    @{ Index = 104; Path = "Packages\Yoghurt"; Label = "yogurt" }
)

foreach ($item in $map) {
    Copy-ToIngredient $item.Index $item.Path $item.Label $logs
}

$logs | Set-Content -Encoding UTF8 $sourceLog
Write-Host "Done. Imported images to: $outDir"
Write-Host "Source log: $sourceLog"
