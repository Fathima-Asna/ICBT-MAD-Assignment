param(
    [string]$Source = "C:\Users\mmhus\OneDrive\Desktop\Asna_app\app-icon.jpg"
)

Add-Type -AssemblyName System.Drawing

$resDir = "C:\Users\mmhus\OneDrive\Desktop\Asna_app\android\app\src\main\res"

if (-not (Test-Path $Source)) {
    Write-Host "Source image not found: $Source"
    exit 1
}

function Resize-Image($inputPath, $outputPath, $size) {
    $img = [System.Drawing.Image]::FromFile($inputPath)
    $bmp = New-Object System.Drawing.Bitmap($size, $size)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.DrawImage($img, 0, 0, $size, $size)
    $g.Dispose()
    $bmp.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    $img.Dispose()
    Write-Host "Created $outputPath (${size}x${size})"
}

$sizes = @{
    "mipmap-mdpi"    = 48
    "mipmap-hdpi"    = 72
    "mipmap-xhdpi"   = 96
    "mipmap-xxhdpi"  = 144
    "mipmap-xxxhdpi" = 192
}

foreach ($folder in $sizes.Keys) {
    $out = [System.IO.Path]::Combine($resDir, $folder, "ic_launcher.png")
    Resize-Image $Source $out $sizes[$folder]
    $outRound = [System.IO.Path]::Combine($resDir, $folder, "ic_launcher_round.png")
    Resize-Image $Source $outRound $sizes[$folder]
}

Write-Host "Icons generated successfully."
