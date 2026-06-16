param(
    [string]$SourceDir = ".\tools\picture\食谱\早餐"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$sourceRoot = Join-Path $root ($SourceDir -replace '^\.[\\/]', '')
$outDir = Join-Path $root "app\src\main\res\drawable-nodpi"
$assetDir = Join-Path $root "app\src\main\assets"
$sourceLog = Join-Path $assetDir "breakfast_recipe_image_sources.txt"

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
New-Item -ItemType Directory -Force -Path $assetDir | Out-Null

if (-not (Test-Path -LiteralPath $sourceRoot)) {
    throw "Breakfast recipe image directory not found: $sourceRoot"
}

function Get-TargetExtension($file) {
    $ext = $file.Extension.ToLowerInvariant()
    if ($ext -eq ".jpeg") { return ".jpg" }
    if ($ext -in @(".jpg", ".png", ".webp")) { return $ext }
    return ".jpg"
}

$files = @(Get-ChildItem -LiteralPath $sourceRoot -File |
    Where-Object { $_.Extension -match '^\.(jpg|jpeg|png|webp)$' } |
    Sort-Object Name)

$logs = New-Object System.Collections.Generic.List[string]
$logs.Add("Breakfast recipe images imported from: $sourceRoot")

$index = 1
foreach ($file in $files) {
    $baseName = "recipe_breakfast_{0:D3}" -f $index
    Get-ChildItem -LiteralPath $outDir -Filter "$baseName.*" | Remove-Item -Force
    $target = Join-Path $outDir "$baseName$(Get-TargetExtension $file)"
    Copy-Item -LiteralPath $file.FullName -Destination $target -Force
    Write-Host "$baseName <- $($file.Name)"
    $logs.Add("$baseName`t$($file.BaseName)`t$($file.FullName)")
    $index++
}

$logs | Set-Content -Encoding UTF8 $sourceLog
Write-Host "Done. Imported breakfast recipe images to: $outDir"
Write-Host "Source log: $sourceLog"
