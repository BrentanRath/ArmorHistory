# Production Release Workflow

This document explains how the automated production release workflow works for the ArmorHistory Minecraft plugin.

## How it Works

When code is merged into the `production` branch, the GitHub Actions workflow automatically:

1. **Builds the plugin** using Gradle with the shadow plugin to create a fat JAR
2. **Creates a GitHub release** with automatic versioning
3. **Uploads the JAR file** to the release as a downloadable asset

## Setting Up the Production Branch

To use this workflow, you need to create a `production` branch:

```bash
# Create and push the production branch
git checkout -b production
git push -u origin production
```

## Triggering a Release

There are two ways to trigger an automated release:

### Method 1: Direct Push to Production
```bash
# Switch to production branch and merge your changes
git checkout production
git merge main
git push origin production
```

### Method 2: Pull Request to Production
1. Create a pull request targeting the `production` branch
2. When the PR is merged, the release workflow will automatically run

## What Gets Released

- **JAR File**: The built plugin JAR with all dependencies included (fat JAR)
- **Location**: The JAR will be in the `build/libs/` folder after building
- **Dependencies**: Includes SQLite JDBC driver, excludes Paper API and Vault API (provided by server)
- **Relocation**: Dependencies are relocated to avoid conflicts with other plugins

## Release Naming

Releases are automatically named with the pattern:
- **Tag**: `v{version}-{date}` (e.g., `v1.0-SNAPSHOT-2024-01-15`)
- **Name**: `ArmorHistory v{version} ({date})`

## Build Configuration

The project now includes:
- **Shadow Plugin**: Creates fat JARs with dependencies
- **Dependency Relocation**: SQLite is relocated to avoid conflicts
- **Automatic Versioning**: Version is extracted from `build.gradle`

## Installation for Server Owners

1. Go to the [Releases page](https://github.com/BrentanRath/ArmorHistory/releases)
2. Download the latest `.jar` file
3. Place it in your server's `plugins/` folder
4. Restart your server

## Development Workflow

1. Develop and test on feature branches
2. Merge features to `main` branch for integration
3. When ready for release, merge `main` to `production`
4. The release happens automatically!