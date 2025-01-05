import com.google.gson.Gson;
import java.io.Serializable;

public class NestedClassSerializationDemo {

    public static void main(String[] args) {
        // Create nested custom objects
        DeepInnerClass deepInner = new DeepInnerClass(42);
        InnerClass inner = new InnerClass("Inner Name", deepInner);
        OuterClass outer = new OuterClass(inner);

        // Create a Request object
        Request request = new Request(
            "/example/path",
            new Object[] { outer },
            new String[] { OuterClass.class.getName() }
        );

        // Serialize the Request object
        String serializedRequest = SerializationUtil.toJSON(request);
        System.out.println("Serialized Request: " + serializedRequest);

        // Deserialize the Request object
        Request deserializedRequest = SerializationUtil.fromJSON(serializedRequest, Request.class);

        // Process the arguments to convert LinkedTreeMap to the correct types
        Object[] arguments = deserializedRequest.getArguments();
        String[] argumentTypes = deserializedRequest.getArgumentTypes();
System.out.println("Length : "+arguments.length);
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = SerializationUtil.convertArgument(arguments[i], argumentTypes[i]);
System.out.println(((OuterClass)arguments[i]).getInnerObject().getClass().getName());
        }

        // Access the deserialized nested objects
        OuterClass deserializedOuter = (OuterClass) arguments[0];
        InnerClass deserializedInner = deserializedOuter.getInnerObject();
        DeepInnerClass deserializedDeepInner = deserializedInner.getDeepInnerObject();

        System.out.println("Deserialized Inner Name: " + deserializedInner.getName());
        System.out.println("Deserialized Deep Inner Value: " + deserializedDeepInner.getValue());
    }

    // Custom classes
    public static class OuterClass implements Serializable {
        private InnerClass innerObject;

        public OuterClass(InnerClass innerObject) {
            this.innerObject = innerObject;
        }

        public InnerClass getInnerObject() {
            return innerObject;
        }
    }

    public static class InnerClass implements Serializable {
        private String name;
        private DeepInnerClass deepInnerObject;

        public InnerClass(String name, DeepInnerClass deepInnerObject) {
            this.name = name;
            this.deepInnerObject = deepInnerObject;
        }

        public String getName() {
            return name;
        }

        public DeepInnerClass getDeepInnerObject() {
            return deepInnerObject;
        }
    }

    public static class DeepInnerClass implements Serializable {
        private int value;

        public DeepInnerClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Request class
    public static class Request implements Serializable {
        private String servicePath;
        private Object[] arguments;
        private String[] argumentTypes;

        public Request(String servicePath, Object[] arguments, String[] argumentTypes) {
            this.servicePath = servicePath;
            this.arguments = arguments;
            this.argumentTypes = argumentTypes;
        }

        public String getServicePath() {
            return servicePath;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public String[] getArgumentTypes() {
            return argumentTypes;
        }
    }

    // Serialization utility class
    public static class SerializationUtil {
        private static final Gson gson = new Gson();

        public static String toJSON(Object object) {
            return gson.toJson(object);
        }

        public static <T> T fromJSON(String json, Class<T> clazz) {
            return gson.fromJson(json, clazz);
        }

        public static Object convertArgument(Object argument, String targetType) {
            if (argument instanceof com.google.gson.internal.LinkedTreeMap) {
                try {
                    Class<?> targetClass = Class.forName(targetType);
                    String json = toJSON(argument);
                    return fromJSON(json, targetClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found: " + targetType, e);
                }
            }
            return argument; // Return as-is if no conversion is needed
        }
    }
}
