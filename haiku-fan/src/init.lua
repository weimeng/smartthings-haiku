local capabilities = require("st.capabilities")
local Driver = require("st.driver")

-- Local imports
local discovery = require("discovery")
local lifecycles = require("lifecycles")
local commands = require("commands")

-- Driver definition
local driver = Driver("Haiku Fan", {
  discovery = discovery.start,
  lifecycle_handlers = lifecycles,
  supported_capabilities = {
    capabilities.refresh,
    capabilities.switch,
    capabilities.switchLevel
  },
  capability_handlers = {
    [capabilities.refresh.ID] = {
      [capabilities.refresh.commands.refresh.NAME] = commands.refresh
    },
    [capabilities.switch.ID] = {
      [capabilities.switch.commands.on.NAME] = commands.switch_on,
      [capabilities.switch.commands.off.NAME] = commands.switch_off
    },
    [capabilities.switchLevel.ID] = {
      [capabilities.switchLevel.commands.setLevel.NAME] = commands.set_level
    },
  },
})

driver:run()
