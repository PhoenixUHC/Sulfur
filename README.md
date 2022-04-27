# Sulfur

A UHC plugin framework

### Why use Sulfur

Creating, managing and gracefully stopping games in your UHC plugins can be a challenge. Sulfur abstracts away a lot
of stuff you used to code manually while giving you a lot of flexibility and customization. Sulfur is being used
by the PhoenixUHC network (coming soon).

### How does it work

Sulfur is made of two main components:

- A plugin that handles your games in the background
- An API that you can use in your plugins to interact with Sulfur

The Sulfur plugin stores each running game in a Redis database underneath the hood. If you're using a proxy such as
Velocity, Waterfall or Bungee, each one of your servers using the Sulfur plugin can access every running game.
