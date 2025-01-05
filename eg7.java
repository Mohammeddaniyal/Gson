import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.*;
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

class eg7psp {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
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

        // Create a Response object
        Response response = new Response();
        response.setSuccess(true);
        response.setResult(w);  // Setting a custom object as the result
        response.setT(new Exception("Something went wrong"));

        // Serialize
        String json = gson.toJson(response);
        System.out.println("Serialized JSON: " + json);

        // Deserialize
        Response deserializedResponse = gson.fromJson(json, Response.class);
        System.out.println("Deserialized Response Success: " + deserializedResponse.isSuccess());
        System.out.println("Deserialized Response Throwable: " + deserializedResponse.getT().getMessage());

        // Handle the deserialized result
        Object result = deserializedResponse.getResult();
        if (result instanceof Whatever) {
            Whatever w1 = (Whatever) result;
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
