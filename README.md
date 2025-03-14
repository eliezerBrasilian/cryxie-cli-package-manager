# Crixie CLI Package Manager

Crixie CLI Package Manager is a lightweight and efficient tool designed to simplify package management on Linux systems.
With Crixie, you can easily install, update, and manage software packages with minimal effort.

## Installation Guide

Follow the steps below to install Crixie CLI Package Manager on your Linux system.

### Step 1: Download the Required File

First, download the necessary installation file using the following command:

```bash
wget https://github.com/eliezerBrasilian/cryxie-cli-package-manager/releases/download/v1.0.0-alpha/setup.zip
```

### Step 2: Extract the Downloaded File

Next, extract the contents of the downloaded ZIP file:

```bash
unzip setup.zip
```

### Step 3: Grant Necessary Permissions

Provide the required permissions to the Crixie setup script:

```bash
chmod +x setup.sh
```

### Step 4: Execute the Installation Script

Run the setup script to install Crixie:

```bash
./setup.sh
```

### Step 5: Verify the Installation

Once the installation is complete, you can verify that Crixie has been successfully installed by running:

```bash
cryxie --help
```

## Usage

After installation, you can start using Crixie to manage your packages. For a list of available commands and options,
use:

```bash
cryxie --help
```

