# Audio output

SoulRadio plays through whatever output Android is already routing to: the phone speaker, wired headphones, a Bluetooth speaker, a Bluetooth car stereo, a Bluetooth hearing aid. That is the whole list, by design.

## Bluetooth is the supported "play it on the speaker in the kitchen" path

Every Google Home / Nest speaker, every modern soundbar, and every halfway-recent Bluetooth speaker accepts a paired phone as an audio source. Pair once in Android settings, then audio from SoulRadio (and everything else on the phone) lands on that speaker until you unpair. There is no in-app step. The lockscreen / Bluetooth media controls work because the `PlaybackService` owns a Media3 `MediaSession`; that's already wired up.

For sound-therapy purists, [ORIGINS.md](../ORIGINS.md) notes that wired beats Bluetooth on fidelity (BT adds compression). True. But for ambient, room-filling listening — which is the use case the manifesto describes — Bluetooth is the right tool.

## What we deliberately do not support

**Google Cast / Chromecast Audio.** Casting to a Google Home speaker over the network would require:

- The Google Cast SDK (a closed-source Google Play Services dependency).
- The `INTERNET` permission, plus `ACCESS_NETWORK_STATE` and `ACCESS_WIFI_STATE`. [CLAUDE.md](../CLAUDE.md) explicitly forbids `INTERNET` and the [manifesto](../MANIFESTO.md) commits to no telemetry and no sale of listening data; pulling in a Google networking SDK quietly undoes that promise even if no listening data is sent today.
- A way for the receiver to fetch the audio. The recordings live inside the APK in `app/src/main/assets/audio/`. To cast them, the app would have to run a local HTTP server or host them remotely. Both are significant departures from "the room, not the app."

Bluetooth covers the user need ("play this on the speaker over there") without any of that. If multi-room synchronised playback across several Google speakers ever becomes a real ask — something Bluetooth genuinely cannot do — that is the moment to revisit, and the right answer then is a deliberate manifesto amendment, not a stealth workaround.

**AirPlay, DLNA, Sonos, Spotify Connect, Roon, etc.** Same reasoning. Each is a network protocol with its own SDK, its own permissions, and its own surface area. Bluetooth is the lowest-common-denominator path that ships zero new code and zero new trust.

**A "now playing on" picker inside the app.** Android already provides one: the system media output switcher on the lockscreen and in the quick-settings notification. Building a second one inside SoulRadio would make the app louder in the user's life for no functional gain.

## Summary

If you want SoulRadio on a speaker: pair the phone to the speaker over Bluetooth. That is the answer. It is not a limitation we are working around — it is the design.
