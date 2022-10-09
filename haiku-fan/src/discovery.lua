local socket = require("socket")
local log = require('log')
local datastore = require("datastore")

-- Local imports
local helpers = require("helpers")

local discovery = {}

function discovery.start(driver, opts, cons)
  -- Get own address
  local s = assert(socket.udp())
  local host = s:getsockname()

  -- Get broadcast address
  local broadcast_address = host:match("%d+%.%d+%.%d+%.") .. "255"

  -- Broadcast discovery message
  log.info("-- Discovery: Sending discovery message")
  s:setoption("broadcast", true)
  s:settimeout(5)
  s:sendto("<ALL;DEVICE;ID;GET>", broadcast_address, 31415)

  -- Receive and store raw responses. Response data is as follows:
  -- (<LABEL>;DEVICE;ID;<MAC_ADDRESS>;FAN,LSERIES)
  local devices = {}
  repeat
    local data, ip, port = s:receivefrom()

    if data then
      log.info("-- Discovery: Received from " .. ip .. " - " .. data)

      devices[ip] = {
        data = data,
      }
    end
  until not data

  s:close()

  -- Parse data
  for ip, values in next, devices do
    local fields = helpers.split_response(values['data'])

    devices[ip]['label'] = fields[1]
    devices[ip]['mac_address'] = fields[4]
    devices[ip]['vendor_provided_label'] = fields[5]
  end

  ds = datastore.init()

  -- Create devices
  for ip, values in next, devices do
    local metadata_fan = {
      type = "LAN",
      device_network_id = values['mac_address'] .. "-FAN",
      label = values['label'],
      profile = "haiku-fan",
      vendor_provided_label = ip, -- Hack to store IP address during creation
    }
    driver:try_create_device(metadata_fan)

    local metadata_light = {
      type = "LAN",
      device_network_id = values['mac_address'] .. "-LGT",
      label = values['label'] .. " Light",
      profile = "haiku-fan-light",
      vendor_provided_label = ip, -- Hack to store IP address during creation
    }
    driver:try_create_device(metadata_light)
  end
end

return discovery
