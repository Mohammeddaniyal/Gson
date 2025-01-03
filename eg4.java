import com.google.gson.*;
import java.util.*;
import java.lang.reflect.Type;

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

class Response {
    private Object result;
    private Throwable throwable;
    private boolean success;

    // Getters and setters
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
class CustomResponseDeserializer implements JsonDeserializer<Response> {
    @Override
    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Response response = new Response();

        // Deserialize primitive fields
        response.setSuccess(jsonObject.get("success").getAsBoolean());
        response.setThrowable(context.deserialize(jsonObject.get("throwable"), Throwable.class));

        // Deserialize 'result' dynamically
        JsonElement resultElement = jsonObject.get("result");
        if (resultElement != null && !resultElement.isJsonNull()) {
            if (resultElement.isJsonArray()) {
                response.setResult(context.deserialize(resultElement, Object[].class));
            } else if (resultElement.isJsonObject()) {
                response.setResult(context.deserialize(resultElement, Map.class)); // Generic map for JSON objects
            } else {
                response.setResult(context.deserialize(resultElement, Object.class)); // Primitive
            }
        }

        return response;
    }
}

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
                if (element.isJsonObject()) {
                    arguments[i] = context.deserialize(element, Map.class); // Generic map for JSON objects
                } else {
                    arguments[i] = context.deserialize(element, Object.class); // Primitive
                }
            }
            request.setArguments(arguments);
        }

        return request;
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
java.util.List<Nothing> list;
}
class eg4psp
{
public static void main(String gg[])
{
Gson gson = new GsonBuilder()
    .registerTypeAdapter(Request.class, new CustomRequestDeserializer())
    .registerTypeAdapter(Response.class, new CustomResponseDeserializer())
    .create();
Whatever w=new Whatever();
Nothing n=new Nothing();
n.f="Hello";
n.k=120;
n.s=true;
w.x=1230;
w.y=12345;
w.list=new java.util.LinkedList<>();
w.list.add(n);

Request request = new Request();
request.setClassPath("com.example.MyClass");
request.setArguments(1, "test", w);

// Convert to JSON
String json = gson.toJson(request);
System.out.println(json);

Request req=gson.fromJson(json,Request.class);

Object ars[]=req.getArguments();
for(Object ar:ars)
{
System.out.println(ar.getClass().getName());
}

}
}


