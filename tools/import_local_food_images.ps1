param(
    [string]$FruitDir = "D:\Android\Picture\train\Fruit",
    [string]$VegetableDir = "D:\Android\Picture\train\Vegetables",
    [string]$PackageDir = "D:\Android\Picture\train\Packages"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$catalogPath = Join-Path $root "app\src\main\java\com\example\smartfood\util\FoodCatalog.java"
$outDir = Join-Path $root "app\src\main\res\drawable-nodpi"
$assetDir = Join-Path $root "app\src\main\assets"
$sourceLog = Join-Path $assetDir "local_food_image_sources.txt"

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
New-Item -ItemType Directory -Force -Path $assetDir | Out-Null

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

function Get-ImageFiles($dir) {
    if (-not (Test-Path -LiteralPath $dir)) {
        Write-Warning "Directory not found: $dir"
        return @()
    }
    return @(Get-ChildItem -LiteralPath $dir -Recurse -File |
        Where-Object { $_.Extension -match '^\.(jpg|jpeg|png|webp)$' } |
        Sort-Object FullName)
}

function Get-TargetExtension($file) {
    $ext = $file.Extension.ToLowerInvariant()
    if ($ext -eq ".jpeg") { return ".jpg" }
    if ($ext -in @(".jpg", ".png", ".webp")) { return $ext }
    return ".jpg"
}

function Find-MatchingFile($files, $usedPaths, $foodName) {
    foreach ($file in $files) {
        if ($usedPaths.ContainsKey($file.FullName)) { continue }
        if ($file.FullName.Contains($foodName)) {
            return $file
        }
    }
    return $null
}

function Find-FallbackFile($files, $usedPaths) {
    foreach ($file in $files) {
        if (-not $usedPaths.ContainsKey($file.FullName)) {
            return $file
        }
    }
    return $null
}

function Import-ImageSet($rows, $dir, $label, $logs) {
    $files = Get-ImageFiles $dir
    $usedPaths = @{}
    if ($files.Count -eq 0) {
        Write-Warning "No images for $label under: $dir"
        foreach ($row in $rows) {
            $baseName = "ingredient_{0:D3}" -f $row.Index
            $logs.Add("$baseName`t$($row.Name)`t$label`tNOT_FOUND")
        }
        return
    }

    foreach ($row in $rows) {
        $baseName = "ingredient_{0:D3}" -f $row.Index
        $file = Find-MatchingFile $files $usedPaths $row.Name
        $mode = "matched"
        if ($null -eq $file) {
            $file = Find-FallbackFile $files $usedPaths
            $mode = "fallback-order"
        }
        if ($null -eq $file) {
            Write-Warning "No available image for $($row.Name) in $label"
            $logs.Add("$baseName`t$($row.Name)`t$label`tNOT_FOUND")
            continue
        }

        $usedPaths[$file.FullName] = $true
        Get-ChildItem -LiteralPath $outDir -Filter "$baseName.*" | Remove-Item -Force
        $target = Join-Path $outDir "$baseName$(Get-TargetExtension $file)"
        Copy-Item -LiteralPath $file.FullName -Destination $target -Force
        Write-Host "$label $($row.Name) -> $target"
        $logs.Add("$baseName`t$($row.Name)`t$label`t$mode`t$($file.FullName)")
    }
}

$allRows = Get-IngredientRows
$logs = New-Object System.Collections.Generic.List[string]
$logs.Add("Local food images imported for SmartFood Android project.")
$logs.Add("FruitDir=$FruitDir")
$logs.Add("VegetableDir=$VegetableDir")
$logs.Add("PackageDir=$PackageDir")

# FoodCatalog indexes:
# 001-060 are vegetable-related categories: leafy/root/gourd/mushroom.
# 061-075 are fruits.
# 103 is milk.
$vegetableRows = @($allRows | Where-Object { $_.Index -ge 1 -and $_.Index -le 60 })
$fruitRows = @($allRows | Where-Object { $_.Index -ge 61 -and $_.Index -le 75 })
$milkRows = @($allRows | Where-Object { $_.Index -eq 103 })

Import-ImageSet $vegetableRows $VegetableDir "vegetable" $logs
Import-ImageSet $fruitRows $FruitDir "fruit" $logs
Import-ImageSet $milkRows $PackageDir "package" $logs

$logs | Set-Content -Encoding UTF8 $sourceLog
Write-Host "Done. Imported local images to: $outDir"
Write-Host "Source log: $sourceLog"
