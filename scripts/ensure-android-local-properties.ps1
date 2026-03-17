$sdk = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$local_props = 'src/android/local.properties'

if (Test-Path $local_props) {
    Write-Output 'src/android/local.properties already exists'
    exit 0
}

if (!(Test-Path $sdk)) {
    Write-Error "Android SDK not found at $sdk. Set it up first or create src/android/local.properties manually."
    exit 1
}

$escaped_sdk = $sdk -replace '\\', '\\'
"sdk.dir=$escaped_sdk" | Set-Content -Encoding ASCII $local_props
Write-Output "Created $local_props"
