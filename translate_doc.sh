#!/bin/bash

# Source directory containing .md files
source_dir="./docs/pages"

# Destination directory for .txt files
destination_dir="./txt_docs"

# Create the destination directory if it doesn't exist
mkdir -p "$destination_dir"

# Loop through all .md files in the source directory
for file in "$source_dir"/*.md; do
    # Check if the file exists
    if [ -f "$file" ]; then
        # Get the filename without extension
        filename=$(basename "$file" .md)
        # Convert .md to .txt
        txt_file="$destination_dir/$filename.txt"
        # Convert and move the file
        pandoc "$file" -o "$txt_file"
        echo "Converted $file to $txt_file"
    fi
done

echo "Conversion complete!"
