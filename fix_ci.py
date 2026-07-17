import sys
with open(sys.argv[1], encoding='utf-8') as f:
    content = f.read()

# Replace all GHCR registry references with ACR
content = content.replace(
    "REGISTRY: ghcr.io\n  IMAGE_NAME: ${{ github.repository }}",
    "REGISTRY: techcuptournaments.azurecr.io\n  IMAGE_NAME: cc-teams-service"
)

content = content.replace(
    "registry: ${{ env.REGISTRY }}\n          username: ${{ github.actor }}\n          password: ${{ secrets.GITHUB_TOKEN }}",
    "registry: ${{ env.REGISTRY }}\n          username: techcuptournaments\n          password: ${{ secrets.ACR_PASSWORD }}"
)

# Fix deploy step image reference
content = content.replace(
    'IMAGE_LOWER=$(echo "ghcr.io/${{ github.repository }}:latest" | tr \'[:upper:]\' \'[:lower:]\')',
    'IMAGE_LOWER="${{ env.REGISTRY }}/cc-teams-service:latest"'
)

with open(sys.argv[1], 'w', encoding='utf-8') as f:
    f.write(content)
print("Fixed to use ACR!")
