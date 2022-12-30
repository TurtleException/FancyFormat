[version-shield]: https://img.shields.io/github/v/release/TurtleException/FancyFormat?include_prereleases
[license-shield]: https://img.shields.io/github/license/TurtleException/FancyFormat

<img align="right" src=".github/FancyFormat.png" height="200" width="200">

[![version-shield]](https://github.com/TurtleException/FancyFormat/releases)
[![license-shield]](LICENSE)

# FancyFormat
A simple library to translate message formats like
[Minecraft legacy formatting codes](https://minecraft.fandom.com/wiki/Formatting_codes),
[Minecraft JSON text format](https://minecraft.fandom.com/wiki/Raw_JSON_text_format) or
[Discord Markdown](https://support.discord.com/hc/en-us/articles/210298617).
The two main goals of this project are performance and lossless translation. Sadly, the latter is not always possible
since different message formats have different visualization features.

To allow applications to use this library for logging a new format is introduced: The **Turtle Format** - A simple, easy
to expand JSON-based format that supports all features that are available in one or more other supported formats. This
new format can be used to store messages without sacrificing information (by storing text in only one format) or using
too much disk space (by storing text in all formats).

### !!! Work in progress !!!
This project is still in development and does not have a stable release yet.
Sadly, I can't provide an exact due date, but it should be finished in early 2023.

Until then, you can use [an alpha version](https://github.com/TurtleException/FancyFormat/releases). But be advised, it is an _alpha_.

## How it's done
To translate one message format to another, the given message & format are used to create an Abstract Syntax Tree (AST),
a data structure that can be used for syntax analysis. This is an easy way to handle messages in-memory. Each node of
the AST holds some information about the formatting of its children **or** is a raw text element. This makes nested
formatting possible and easy to interpret / parse.

**So, in less fancy words**, a message is converted into some data structure, which can then be used to translate the
initial format to some other format with minimal loss of information.

## Example
Let's say we have this message from Discord:

<img src=".github/example-discord.png" width="750">

The raw content of that message looks like this:
```
Hey <@916094119758139413>, look at _this cool **formatting!**_
```
Regardless of what format we want to translate this message to, we first need to create the message AST. Here's a simple
visualization of the implementation:

<img src=".github/example-ast.png" width="750">

Ok, so we now can display this message in any supported format. Like, for example, the Minecraft JSON format:
```json
[
  {
    "text": "Hey "
  },
  {
    "text":  "@TurtleBot",
    "color": "aqua"
  },
  {
    "text": ", look at "
  },
  {
    "text": "this cool ",
    "italic": true
  },
  {
    "text": "formatting!",
    "bold": true,
    "italic": true
  }
]
```
Or alternatively the legacy formatting codes:
```
Hey §b@TurtleBot§r, look at §othis cool §lformatting!
```
Both of which would look like this in-game:

<img src=".github/example-minecraft.png" width="750">

# Usage
To translate between formats you first need to create a `FancyFormatter` - A builder class that manages formatting rules.
Now you can create `FormatText` objects. These are an abstract representation of a message that store data as described
[above](#example). From that object you can parse any supported format.

```java
FancyFormatter formatter = new FancyFormatter();

// create the abstract representation
FormatText text = formatter.newText("A **Discord** message", Format.DISCORD);

// translate the message into any format
String mcLegacyMessage = text.toString(Format.MINECRAFT_LEGACY);
String mcJsonMessage   = text.toString(Format.MINECRAFT_JSON);
String turtleMessage   = text.toString(Format.TURTLE);
// ...
```
Currently, there are 5 supported formats: `PLAINTEXT`, `TURTLE`, `DISCORD`, `MINECRAFT_JSON` and `MINECRAFT_LEGACY`

To enable storing messages with minimal capacity requirements there is also native format. It stores the message in its
original format prepended by an indicator as to what format that is. This way messages can be restored into different
formats universally.
Parsing a message to its native format doesn't require a `FancyFormatter`, but restoring them does as the message will
be parsed into a `FormatText`:
```java
// create native string
String nativeString = Format.DISCORD.toNative("A §lMinecraft§r message");

// restore message
FormatText text = formatter.ofNative(nativeString);
```
