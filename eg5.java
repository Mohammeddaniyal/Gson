import com.google.gson.*;
import java.util.*;
import java.lang.reflect.Type;

class CustomRequestDeserializer implements JsonDeserializer<Request> {
    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Request request = new Request();

        // Deserialize 'classPath'
        request.setClassPath(jsonObject.get("classPath").getAsString());

        // Deserialize 'arguments' dynamically
        JsonArray argumentsArray = jsonObject.getAsJsonArray("arguments");
        if (argumentsArray != null) {
            Object[] arguments = new Object[argumentsArray.size()];
            for (int i = 0; i < argumentsArray.size(); i++) {
                JsonElement element = argumentsArray.get(i);

                // Dynamically determine the type
                if (element.isJsonPrimitive()) {
                    arguments[i] = context.deserialize(element, Object.class); // Primitive
                } else if (element.isJsonObject()) {
                    // Use type-specific deserialization for custom classes
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("x") && obj.has("y") && obj.has("list")) {
                        arguments[i] = context.deserialize(element, Whatever.class);
                    } else if (obj.has("f") && obj.has("k") && obj.has("s")) {
                        arguments[i] = context.deserialize(element, Nothing.class);
                    } else {
                        arguments[i] = context.deserialize(element, Map.class); // Generic map for unknown objects
                    }
                } else if (element.isJsonArray()) {
                    arguments[i] = context.deserialize(element, Object[].class);
                }
            }
            request.setArguments(arguments);
        }

        return request;
    }
}
class eg5psp
{
public static void main(String[] args) {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Request.class, new CustomRequestDeserializer())
        .registerTypeAdapter(Response.class, new CustomResponseDeserializer())
        .create();

    Whatever w = new Whatever();
    Nothing n = new Nothing();
    n.f = "Hello";
    n.k = 120;
    n.s = true;
    w.x = 1230;
    w.y = 12345;
    w.list = new java.util.LinkedList<>();
    w.list.add(n);

    Request request = new Request();
    request.setClassPath("com.example.MyClass");
    request.setArguments(1, "test", w);

    // Convert to JSON
    String json = gson.toJson(request);
    System.out.println(json);

    // Deserialize JSON
    Request req = gson.fromJson(json, Request.class);

    // Check deserialized arguments
    Object[] argsArray = req.getArguments();
    for (Object arg : argsArray) {
        System.out.println(arg.getClass().getName());
        if (arg instanceof Whatever) {
            Whatever deserializedWhatever = (Whatever) arg;
            System.out.println("x: " + deserializedWhatever.x);
            System.out.println("y: " + deserializedWhatever.y);
            System.out.println("List size: " + deserializedWhatever.list.size());
        } else if (arg instanceof Nothing) {
            Nothing deserializedNothing = (Nothing) arg;
            System.out.println("f: " + deserializedNothing.f);
            System.out.println("k: " + deserializedNothing.k);
            System.out.println("s: " + deserializedNothing.s);
        }
    }
}
}
