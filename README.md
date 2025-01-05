# Gson Java Examples for Serialization and Deserialization

## Introduction

This repository contains various examples demonstrating how to serialize and deserialize objects using Gson in Java. The main purpose of this collection is to provide practical solutions for handling complex serialization and deserialization scenarios, especially when dealing with custom classes and nested objects.

### Why This Repository Exists

During the development of my custom project **[NetworkFrame](https://github.com/yourusername/NetworkFrame)**, I encountered a specific error related to the serialization and deserialization of arguments between the client and server. In my project, the client sends arguments wrapped in a `Request` class, and these arguments are stored in an `Object[]` array. However, when deserializing these arguments on the server side, Gson defaulted to deserializing my custom classes into `com.google.gson.internal.LinkedTreeMap` because it did not recognize the custom class type. This resulted in errors and unexpected behavior.

At first, I tried several approaches to resolve this issue, including passing `Request.class` during serialization and deserialization, but that did not work as expected. After experimenting with different solutions, I arrived at a working solution: creating a **custom serializer** and **custom deserializer** for my `Request` class in my **NetworkFrame**.

### Key Concepts

- **Serialization**: The process of converting a Java object into a JSON string.
- **Deserialization**: The reverse process of converting a JSON string back into a Java object.
- **Custom Serializer and Deserializer**: In cases where Gson does not recognize custom classes during deserialization, custom serializers and deserializers can be implemented to ensure the correct transformation of objects.

### Problem Encountered

The issue arose when I was assigning arguments for the client-side to the server-side, and these arguments were wrapped inside a `Request` class. The `Request` class contained an `Object[]` array to hold these arguments. Upon deserialization, Gson did not recognize my custom classes and defaulted to deserializing them into `LinkedTreeMap`, which led to errors and incorrect deserialization.

### Solution

The solution I implemented involved creating custom serializers and deserializers to ensure that Gson could properly handle my custom classes during serialization and deserialization. The main goal of this repository is to provide working examples of how to tackle similar issues, specifically in the context of network communication where complex object structures are serialized and deserialized.

---

## Examples in This Repository

### 1. **Nested Class Serialization and Deserialization**

This example demonstrates how to serialize and deserialize nested custom classes. It includes:
- **OuterClass**: Contains an instance of `InnerClass`.
- **InnerClass**: Contains an instance of `DeepInnerClass`.
- **DeepInnerClass**: A deeply nested class with a simple field.

This example shows how to properly handle nested objects and how Gson can be used to serialize and deserialize them correctly, even when the objects are deeply nested.

### 2. **Handling Arguments in a Request Class**

The core issue I encountered was with serializing and deserializing the `Request` class. The arguments for the server were wrapped inside a `Request` object, which included an `Object[]` array. Gson did not recognize the custom classes, so I had to create a custom serializer and deserializer to handle the conversion correctly.

---

## Link to My NetworkFrame Project

For more details about the **NetworkFrame** project, where this issue was encountered and solved, visit the repository:

[NetworkFrame Repository](https://github.com/yourusername/NetworkFrame)

---

## Installation

To use these examples, ensure that you have the following dependencies in your project:

- **Gson**: You can add Gson to your project using Maven or Gradle.

For Maven:
```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.8</version>
</dependency>
For Gradle:
implementation 'com.google.code.gson:gson:2.8.8'

