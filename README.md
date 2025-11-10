A **server-side only** mod that runs all calculations of the railway network on a **separate thread** parallel to the server tick, which greatly improves performance, especially in large networks.

<img width="2430" height="757" alt="comparison" src="https://github.com/user-attachments/assets/366bda07-5e2f-49f2-a313-af1e70660617" />

## ✅ Issues & Compatibility
This mod should be compatible with most other addons and performance mods.
No problems or limitations are known when using this mod. However, should any issues arise, this mod can be safely removed and everything will return to normal.

## 🧩 How does it work?
Usually, the railway network is calculated immediately after the other tasks of the server tick loop (e.g. BlockEntities, Entities, Players, Chunks, etc.). Under high server load, for example, due to many BlockEntities and a large rail network, this can result in very long tick times.

This mod starts calculating the railway network in parallel with the other tasks right at the beginning of a new server tick, thus reducing the overall time. To keep the game synchronized, both tasks wait on each other until they are finished before the tick ends.

Extensive tests on a large server with nearly 500 trains and a total of 200 mods have shown that this improves performance significantly.

## ⚠️ Please note!
To protect your world from damage, you should always create a backup of your world before installing an update of this mod. Alpha versions in particular may contain critical bugs!
