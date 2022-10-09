-- Local imports
local helpers = require("helpers")

local lifecycle_handler = {}

function lifecycle_handler.init(driver, device)
  local ip_address = device.st_store["vendor_provided_label"]
  local mac_address = string.sub(device.st_store["device_network_id"], 1, -5)

  -- Set up device MAC and IP address
  device:set_field('ip_address', ip_address)
  device:set_field('mac_address', mac_address)

  -- Set initial device state
  helpers.refresh_device_state(device)
end

-- function lifecycle_handler.added(driver, device)
-- end

-- function lifecycle_handler.doConfigure(driver, device)
-- end

-- function lifecycle_handler.driverSwitches(driver, device)
-- end

-- function lifecycle_handler.removed(driver, device)
-- end

return lifecycle_handler
