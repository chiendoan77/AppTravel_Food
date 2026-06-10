param(
    [switch]$Force
)

# Copies local.properties.example to local.properties
$dest = Join-Path -Path (Get-Location) -ChildPath 'local.properties'
$src = Join-Path -Path (Get-Location) -ChildPath 'local.properties.example'

if (Test-Path $dest -and -not $Force) {
    Write-Host "local.properties already exists. Use -Force to overwrite." -ForegroundColor Yellow
    exit 1
}

Copy-Item -Path $src -Destination $dest -Force:$Force
Write-Host "Created local.properties from local.properties.example. Edit it and add your keys." -ForegroundColor Green