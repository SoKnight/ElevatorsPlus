#!/bin/bash

# Setup vars
plugin_name="ElevatorsPlus"
shaded_jar="ElevatorsPlus.jar"

# Init colors
red=`tput setaf 1`
green=`tput setaf 2`
yellow=`tput setaf 3`
cyan=`tput setaf 6`
reset=`tput sgr0`

# Building
echo "${cyan}Clean-up and bilding $plugin_name with Maven..."
if mvn clean install
then
	echo "${green}Build success."
else
	echo "${red}[ERROR] Failed build this plugin :("
	exit 1
fi

# Deploying
echo "${cyan}Copying compiled jar into test server directory as $plugin_name.jar..."
if cp target/$shaded_jar $HOME/Development/Servers/Paper\ 1.15.2/plugins/$plugin_name.jar 
then
	echo "${green}Successfully copied, reload plugin ;)"
else
	echo "${red}[ERROR] Failed copy compiled plugin jar into server plugins directory."
	exit 2
fi