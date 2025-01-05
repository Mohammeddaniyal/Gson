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

class Response {
    private boolean success;
    private Object result;
    private Throwable t;

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getT() {
        return t;
    }

    public void setT(Throwable t) {
        this.t = t;
    }
}

class CustomResponseDeserializer implements JsonDeserializer<Response> {
    @Override
    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Response response = new Response();

        // Deserialize 'success'
        response.setSuccess(jsonObject.get("success").getAsBoolean());

        // Deserialize 'result' dynamically
        JsonElement resultElement = jsonObject.get("result");
        if (resultElement != null && !resultElement.isJsonNull()) {
            // Check the type of 'result' and deserialize accordingly
            String resultType = jsonObject.get("resultType").getAsString(); // Expecting a 'resultType' field
            try {
                Class<?> resultClass = Class.forName(resultType);
                response.setResult(context.deserialize(resultElement, resultClass));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown class for result: " + resultType, e);
            }
        }

        // Deserialize 't' (Throwable)
        JsonElement throwableElement = jsonObject.get("t");
        if (throwableElement != null && !throwableElement.isJsonNull()) {
            String throwableClass = jsonObject.get("throwableClass").getAsString();
            try {
                Class<?> throwableClassObj = Class.forName(throwableClass);
                response.setT((Throwable) context.deserialize(throwableElement, throwableClassObj));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown class for throwable: " + throwableClass, e);
            }
        }

        return response;
    }
}

class CustomResponseSerializer implements JsonSerializer<Response> {
    @Override
    public JsonElement serialize(Response src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize 'success'
        jsonObject.addProperty("success", src.isSuccess());

        // Serialize 'result'
        if (src.getResult() != null) {
            jsonObject.add("result", context.serialize(src.getResult()));
            jsonObject.addProperty("resultType", src.getResult().getClass().getName()); // Store the class name for dynamic deserialization
        }

        // Serialize 't' (Throwable)
        if (src.getT() != null) {
            jsonObject.add("t", context.serialize(src.getT()));
            jsonObject.addProperty("throwableClass", src.getT().getClass().getName()); // Store the class name of the throwable
        }

        return jsonObject;
    }
}

 class eg9psp {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Request.class, new CustomRequestDeserializer())
            .registerTypeAdapter(Request.class, new CustomRequestSerializer())
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
System.out.println("REQUEST PART ENDS");

         w = new Whatever();
         n = new Nothing();
        n.f = "Hello";
        n.k = 120;
        n.s = true;
        w.x = 1230;
        w.y = 12345;
        w.n = n;
        w.list = new LinkedList<>();
        w.list.add(n);

        // Create a Response object
        Response response = new Response();
        response.setSuccess(true);
        response.setResult(w);  // Setting a custom object as the result
        response.setT(new Exception("Something went wrong"));

        // Serialize
        json = gson.toJson(response);
        System.out.println("Serialized JSON: " + json);

        // Deserialize
        Response deserializedResponse = gson.fromJson(json, Response.class);
        System.out.println("Deserialized Response Success: " + deserializedResponse.isSuccess());
        System.out.println("Deserialized Response Throwable: " + deserializedResponse.getT().getMessage());

        // Handle the deserialized result
        Object result = deserializedResponse.getResult();
        if (result instanceof Whatever) {
             w1 = (Whatever) result;
            System.out.println("Deserialized Result - Whatever: " + w1.x + ", " + w1.y);
java.util.List<Nothing> l=w1.list;
for(Nothing k:l)
{
System.out.println("Nothing (list): "+k.f+","+k.k+","+k.s);
}

        }

        n = new Nothing();
        n.f = "I like it";
        n.k = 786;
        n.s = false;


response = new Response();
        response.setSuccess(true);
        response.setResult(n);  // Setting a custom object as the result
        response.setT(new Exception("Oppppps!!! Something went wrong"));

        // Serialize
 json = gson.toJson(response);
        System.out.println("Serialized JSON: " + json);

        // Deserialize
         deserializedResponse = gson.fromJson(json, Response.class);
        System.out.println("Deserialized Response Success: " + deserializedResponse.isSuccess());
        System.out.println("Deserialized Response Throwable: " + deserializedResponse.getT().getMessage());

        // Handle the deserialized result
        	 result = deserializedResponse.getResult();
       if (result instanceof Nothing) {
    Nothing k= (Nothing) result;
            
System.out.println("Nothing (list): "+k.f+","+k.k+","+k.s);

        }




    }
}


