# MapPlugin
Spigot plugin client for the MapService.


##Commands

###/maps 
Any command requires exorath.maps.cmd permission

####/maps help
#####No permissions required
Displays help information in chat



####/maps list
#####No permissions required
Opens the map GUI containing a list of maps with their basic information. 

Clicking on the map will open the environment menu.

Clicking on an environment in the environment menu opens the versions menu. 

Clicking on a version runs /maps load {mapId} {envId} {versionId}



####/maps save {mapId} {envId}
#####Requires exorath.maps.save.{mapId}.{envId}(wildcards allowed)
If there is a version of this mapId currently open on the server, it will be saved.


####/maps create {mapId}
#####Requires exorath.maps.create.{mapId}  (wildcards allowed)
Loads a new map in, this is not saved before you call the save command.

You can use your own generator by already having the world loaded in with such generator. 
By default a void world generator will be used.

####/maps load {mapId} {envId} ({versionId})
#####Requires exorath.maps.load.{mapId}.{envId}
Loads a map from the cloud, by default it will use the latest version of this map. The player will be teleported to the center of this map.

####/maps unload {mapId}
#####Requires exorath.maps.unload.{mapId}.{envId}
Unloads the map if you have permission to unload that envId, this will unload and remove the map completely from the server, though any saved versions remain.


