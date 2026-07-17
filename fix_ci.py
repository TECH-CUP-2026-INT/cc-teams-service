import sys
with open(sys.argv[1], encoding='utf-8') as f:
    content = f.read()

# Current broken state has both blocks - remove the incomplete first one
old = """          az containerapp update \\
            --name teams-service \\
            --resource-group techcup \\
            IMAGE_LOWER=$(echo "ghcr.io/${{ github.repository }}:latest" | tr '[:upper:]' '[:lower:]')
          az containerapp update \\
            --name teams-service \\
            --resource-group techcup \\
            --image "$IMAGE_LOWER\""""

new = """          IMAGE_LOWER=$(echo "ghcr.io/${{ github.repository }}:latest" | tr '[:upper:]' '[:lower:]')
          az containerapp update \\
            --name teams-service \\
            --resource-group techcup \\
            --image "$IMAGE_LOWER\""""

content = content.replace(old, new)
with open(sys.argv[1], 'w', encoding='utf-8') as f:
    f.write(content)
print("Fixed!")
