param(
    [string]$SourceDir = "D:\Android\Picture\train\Fruit"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$catalogPath = Join-Path $root "app\src\main\java\com\example\smartfood\util\FoodCatalog.java"
$outDir = Join-Path $root "app\src\main\res\drawable-nodpi"
$assetDir = Join-Path $root "app\src\main\assets"
$sourceLog = Join-Path $assetDir "local_fruit_image_sources.txt"

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
New-Item -ItemType Directory -Force -Path $assetDir | Out-Null

if (-not (Test-Path -LiteralPath $SourceDir)) {
    throw "Source directory not found: $SourceDir"
}

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

function Get-ImageFiles {
    Get-ChildItem -LiteralPath $SourceDir -Recurse -File |
        Where-Object { $_.Extension -match '^\.(jpg|jpeg|png|webp)$' } |
        Sort-Object FullName
}

function Get-TargetExtension($file) {
    $ext = $file.Extension.ToLowerInvariant()
    if ($ext -eq ".jpeg") { return ".jpg" }
    if ($ext -in @(".jpg", ".png", ".webp")) { return $ext }
    return ".jpg"
}

function Find-MatchingFile($files, $usedPaths, $fruitName) {
    foreach ($file in $files) {
        if ($usedPaths.ContainsKey($file.FullName)) { continue }
        if ($file.FullName.Contains($fruitName)) {
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

$rows = Get-IngredientRows
$fruitRows = $rows | Where-Object { $_.Index -ge 61 -and $_.Index -le 75 }
$files = @(Get-ImageFiles)
if ($files.Count -eq 0) {
    throw "No image files found under: $SourceDir"
}

$usedPaths = @{}
$logs = New-Object System.Collections.Generic.List[string]
$logs.Add("Local fruit images imported from: $SourceDir")

foreach ($fruit in $fruitRows) {
    $baseName = "ingredient_{0:D3}" -f $fruit.Index
    $file = Find-MatchingFile $files $usedPaths $fruit.Name
    $mode = "matched"
    if ($null -eq $file) {
        $file = Find-FallbackFile $files $usedPaths
        $mode = "fallback-order"
    }
    if ($null -eq $file) {
        Write-Warning "No available local image for $($fruit.Name)"
        $logs.Add("$baseName`t$($fruit.Name)`tNOT_FOUND")
        continue
    }

    $usedPaths[$file.FullName] = $true
    Get-ChildItem -LiteralPath $outDir -Filter "$baseName.*" | Remove-Item -Force
    $target = Join-Path $outDir "$baseName$(Get-TargetExtension $file)"
    Copy-Item -LiteralPath $file.FullName -Destination $target -Force
    Write-Host "$($fruit.Name) -> $target"
    $logs.Add("$baseName`t$($fruit.Name)`t$mode`t$($file.FullName)")
}

$logs | Set-Content -Encoding UTF8 $sourceLog
Write-Host "Done. Imported local fruit images to: $outDir"
Write-Host "Source log: $sourceLog"
