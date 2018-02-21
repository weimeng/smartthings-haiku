# Haiku fan device handler for SmartThings

This device handler was written with the intent of enabling voice control using
the Google Assistant SmartThings integration.

## Installation

Install manually or using Github integration with these settings:

```
Owner: weimeng
Name: smartthings-haiku
Branch: master
```

## Setup

1. Using the web interface, add a new device. Make sure to select the `Haiku
   Fan` device handler!
2. In the device page, click on edit link next to "Preferences".
3. Enter your fan's MAC address and IP address.
4. In the SmartThings app, click on Add Things.
5. Your fan should now be paired.

## Usage

This device handler creates a hidden child device named `${fan name} light`. If
you name your fan "Study Room Fan", a "Study Room Fan Light" hidden device will
be created. It will not appear in your Things list, but can be added as a
switch to Smart Apps, such as the Google Assistant integration.

In the voice control examples below, substitute "fan" with your actual fan name.

### Fan

* Turn on fan
* Turn off fan
* Set fan to 50%
* Turn up fan
* Turn down fan
* Brighten fan
* Dim fan

### Light

* Turn on fan light
* Turn off fan light
* Set fan light to 50%
* Turn up fan light
* Turn down fan light
* Brighten fan light
* Dim fan light

## Limitations

This device handler is unable to detect the current state of your fan's motor
and lights. If you adjust your fan with the remote control or Haiku's app, it
will go out of sync with SmartThings.