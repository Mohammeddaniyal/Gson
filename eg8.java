import java.util.*;
import com.google.gson.*;
import java.lang.reflect.Type;

// Request class
class Request {
    private Object[] arguments;
    private String classPath;

    // Getters and setters
    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}

// Custom Serializer for Request
class CustomRequestSerializer implements JsonSerializer<Request> {
    @Override
    public JsonElement serialize(Request src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize 'classPath'
        jsonObject.addProperty("classPath", src.getClassPath());

        // Serialize 'arguments'
        JsonArray argumentsArray = new JsonArray();
        for (Object arg : src.getArguments()) {
            JsonObject argObject = new JsonObject();
            argObject.addProperty("type", arg.getClass().getName()); // Store the type
            argObject.add("value", context.serialize(arg)); // Store the serialized value
            argumentsArray.add(argObject);
        }
        jsonObject.add("arguments", argumentsArray);

        return jsonObject;
    }
}

// Custom Deserializer for Request
class CustomRequestDeserializer implements JsonDeserializer<Request> {
    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Request request = new Request();

        // Deserialize 'classPath'
        request.setClassPath(jsonObject.get("classPath").getAsString());

        // Deserialize 'arguments'
        JsonArray argumentsArray = jsonObject.getAsJsonArray("arguments");
        Object[] arguments = new Object[argumentsArray.size()];
        for (int i = 0; i < argumentsArray.size(); i++) {
            JsonObject argObject = argumentsArray.get(i).getAsJsonObject();
            String type = argObject.get("type").getAsString(); // Get the type
            try {
                Class<?> argClass = Class.forName(type); // Dynamically load the class
                arguments[i] = context.deserialize(argObject.get("value"), argClass); // Deserialize the value
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown class: " + type, e);
            }
        }
        request.setArguments(arguments);

        return request;
    }
}

// Example custom classes
class Nothing {
    String f;
    int k;
    boolean s;
}

class Whatever {
    int x;
    int y;
    Nothing n;
    List<Nothing> list;
}

// Main class to demonstrate functionality
 class eg8psp {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Request.class, new CustomRequestDeserializer())
            .registerTypeAdapter(Request.class, new CustomRequestSerializer())
            .create();

        // Example objects
        Whatever w = new Whatever();
        Nothing n = new Nothing();
        n.f = "Hello";
        n.k = 120;
        n.s = true;
        w.x = 1230;
        w.y = 12345;
        w.n = n;
        w.list = new LinkedList<>();
        w.list.add(n);

        // Create request
        Request request = new Request();
        request.setClassPath("com.example.MyClass");
        request.setArguments(1, "test", w);

        // Serialize
        String json = gson.toJson(request);
        System.out.println("Serialized JSON: " + json);

        // Deserialize
        Request deserializedRequest = gson.fromJson(json, Request.class);
        System.out.println("Deserialized Request ClassPath: " + deserializedRequest.getClassPath());

        // Access deserialized arguments
        Whatever w1 = null;
        for (Object arg : deserializedRequest.getArguments()) {
            System.out.println("Argument: " + arg + ", Type: " + arg.getClass().getName());
            if (arg instanceof Whatever) {
                w1 = (Whatever) arg;
            }
        }

        // Verify deserialized data
        if (w1 != null) {
            Nothing n1 = w1.n;
            List<Nothing> l = w1.list;
            System.out.println("Nothing: " + n1.f + ", " + n1.k + ", " + n1.s);

            for (Nothing k : l) {
                System.out.println("Nothing (list): " + k.f + ", " + k.k + ", " + k.s);
            }
        }
    }
}
