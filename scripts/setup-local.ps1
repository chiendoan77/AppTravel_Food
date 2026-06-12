param(
    [switch]$Force
)

$projectRoot = Split-Path -Parent $PSScriptRoot
$dest = Join-Path -Path $projectRoot -ChildPath 'local.properties'
$src = Join-Path -Path $projectRoot -ChildPath 'local.properties.example'

if ((Test-Path $dest) -and -not $Force) {
    Write-Host "local.properties already exists. Use -Force to overwrite." -ForegroundColor Yellow
    exit 1
}

Copy-Item -Path $src -Destination $dest -Force:$Force

$sdkCandidates = @(
    $env:ANDROID_SDK_ROOT,
    $env:ANDROID_HOME,
    $(if ($env:LOCALAPPDATA) { Join-Path $env:LOCALAPPDATA 'Android\Sdk' })
) | Where-Object { $_ -and (Test-Path $_) }

$sdkPath = $sdkCandidates | Select-Object -First 1
if ($sdkPath) {
    $escapedSdkPath = $sdkPath.Replace('\', '\\').Replace(':', '\:')
    Add-Content -Path $dest -Value "`nsdk.dir=$escapedSdkPath"
    Write-Host "Android SDK found: $sdkPath" -ForegroundColor Green
} else {
    Write-Host "Android SDK was not found automatically." -ForegroundColor Yellow
    Write-Host "Open Android Studio > Settings > Android SDK, then add sdk.dir to local.properties."
}

Write-Host "Created local.properties. Add your API configuration before building." -ForegroundColor Green
