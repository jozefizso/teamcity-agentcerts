TeamCity Agent - Code Signing Certificates
==============================================================

This plugin extends TeamCity Build Agent with [Configuration Parameters](http://confluence.jetbrains.net/display/TCD6/Configuration+and+Build+Parameters)
based on the list of Code Signing certificates in the user and computer certificates store on Windows.

## Contributing

### 1. Implement

This project contains 3 modules:

* agentcerts-server
* agentcerts-agent
* agentcerts-common

They contain code for server and agent parts of your plugin and a common part, available for both (agent and server). When implementing components for server and agent parts, do not forget to update spring context files under `main/resources/META-INF`. Otherwise your component may be not loaded. See TeamCity documentation for details on plugin development.

### 2. Build
Issue `mvn package` command from the root project to build your plugin. Resulting package **teamcity-agent-certs.zip** will be placed in **target** directory. 

### 3. Install
To install the plugin, put zip archive to **plugins** directory under TeamCity data directory. If you only changed agent-side code of your plugin, the upgrade will be perfomed 'on the fly' (agents will upgrade when idle). If common or server-side code has changed, restart the server.


## License

Copyright (c) 2015 Jozef Izso.

Licensed under [MIT License](LICENSE).