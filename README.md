# FritzBoxConnect-J

App to query local AVM Fritz!Box (expected to be at `fritz.box` in network) smarthome sensors.

See official [avm documentation](https://avm.de/service/schnittstellen/) for detailed information.

Service `de.codingForFun.el.fritzbox.FritzConnectService` takes care of login and sessionId management

rename /secret/app.secret.demo to /secret/app.secret and add your fritzbox pw with key `fritz.user.pw`

# ToDos and open issues
currently uses an accept all ssl handler, this should be changed to reading
and using a local copy of fritzbox ssl certificate