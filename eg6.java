import java.util.*;
import com.google.gson.*;
import java.lang.reflect.Type;
class Request {
    private Object[] arguments;
    private String[] argumentTypes; // Fully qualified class names
    private String classPath;

    // Getters and setters
    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }

    public String[] getArgumentTypes() {
        return argumentTypes;
    }

    public void setArgumentTypes(String... argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}
class CustomRequestDeserializer implements JsonDeserializer<Request> {
    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Request request = new Request();

        // Deserialize 'classPath'
        request.setClassPath(jsonObject.get("classPath").getAsString());

        // Deserialize 'argumentTypes'
        JsonArray typesArray = jsonObject.getAsJsonArray("argumentTypes");
        String[] argumentTypes = new String[typesArray.size()];
        for (int i = 0; i < typesArray.size(); i++) {
            argumentTypes[i] = typesArray.get(i).getAsString();
        }
        request.setArgumentTypes(argumentTypes);

        // Deserialize 'arguments' dynamically
        JsonArray argumentsArray = jsonObject.getAsJsonArray("arguments");
        Object[] arguments = new Object[argumentsArray.size()];
        for (int i = 0; i < argumentsArray.size(); i++) {
            try {
                // Dynamically load the class
                Class<?> argClass = Class.forName(argumentTypes[i]);
                arguments[i] = context.deserialize(argumentsArray.get(i), argClass);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown class: " + argumentTypes[i], e);
            }
        }
        request.setArguments(arguments);

        return request;
    }
}
class CustomRequestSerializer implements JsonSerializer<Request> {
    @Override
    public JsonElement serialize(Request src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize 'classPath'
        jsonObject.addProperty("classPath", src.getClassPath());

        // Serialize 'arguments'
        JsonArray argumentsArray = new JsonArray();
        JsonArray typesArray = new JsonArray();
        for (Object arg : src.getArguments()) {
            argumentsArray.add(context.serialize(arg));
            typesArray.add(arg.getClass().getName()); // Add the fully qualified class name
        }
        jsonObject.add("arguments", argumentsArray);
        jsonObject.add("argumentTypes", typesArray);

        return jsonObject;
    }
}

class ThrowableAdapter extends TypeAdapter<Throwable> {
    @Override
    public void write(JsonWriter out, Throwable value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("message").value(value.getMessage());
        out.name("type").value(value.getClass().getName());
        out.name("stackTrace").value(Arrays.toString(value.getStackTrace()));
        out.endObject();
    }

    @Override
    public Throwable read(JsonReader in) throws IOException {
        in.beginObject();
        String message = null;
        String type = null;
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "message":
                    message = in.nextString();
                    break;
                case "type":
                    type = in.nextString();
                    break;
                case "stackTrace":
                    in.nextString(); // Ignore stack trace during deserialization
                    break;
            }
        }
        in.endObject();
        return new RuntimeException(message); // You can customize this to create specific exceptions if needed
    }
}

class Nothing
{
String f;
int k;
boolean s;
}
class Whatever
{
int x;
int y;
Nothing n;
java.util.List<Nothing> list;
}

class eg6psp
{
public static void main(String[] args) {
Gson gson = new GsonBuilder()
    .registerTypeAdapter(Throwable.class, new ThrowableAdapter())
    .registerTypeAdapter(Response.class, new CustomResponseDeserializer())
    .registerTypeAdapter(Response.class, new CustomResponseSerializer())
    .create();

    // Example objects
    Whatever w = new Whatever();
    Nothing n = new Nothing();
    n.f = "Hello";
    n.k = 120;
    n.s = true;
    w.x = 1230;
    w.y = 12345;
w.n=n;
    w.list = new LinkedList<>();
    w.list.add(n);

    // Create request
    Request request = new Request();
    request.setClassPath("com.example.MyClass");
    request.setArguments(1, "test", w);
    request.setArgumentTypes(Integer.class.getName(), String.class.getName(), Whatever.class.getName());

    // Serialize
    String json = gson.toJson(request);
    System.out.println("Serialized JSON: " + json);

    // Deserialize
    Request deserializedRequest = gson.fromJson(json, Request.class);
    System.out.println("Deserialized Request ClassPath: " + deserializedRequest.getClassPath());
Whatever w1=null;
    for (Object arg : deserializedRequest.getArguments()) {
        System.out.println("Argument: " + arg + ", Type: " + arg.getClass().getName());
if(arg.getClass().getName().equals("Whatever"))
{
System.out.println("hi");
w1=(Whatever)arg;    
}
}
Nothing n1=w1.n;
java.util.List<Nothing> l=w1.list;
System.out.println("Nothing : "+n1.f+","+n1.k+","+n1.s);

for(Nothing k:l)
{
System.out.println("Nothing (list): "+k.f+","+k.k+","+k.s);
}

}

}