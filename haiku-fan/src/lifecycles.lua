local capabilities = require("st.capabilities")

local lifecycle_handler = {}

function lifecycle_handler.added(driver, device)
  local ip_address = device.st_store["vendor_provided_label"]
  local mac_address = string.sub(device.st_store["device_network_id"], 1, -5)

  device:set_field('ip_address', ip_address)
  device:set_field('mac_address', mac_address)
end

function lifecycle_handler.doConfigure(driver, device)
   device:emit_event(capabilities.switch.switch.on())
   device:emit_event(capabilities.switchLevel.level(100))
end

return lifecycle_handler
