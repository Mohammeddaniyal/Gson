import com.google.gson.*;
class Response implements java.io.Serializable
{
private String servicePath;
private Object []arguments;
public void setServicePath(String servicePath)
{
this.servicePath=servicePath;
}
public String getServicePath()
{
return this.servicePath;
}
public void setArguments(Object ...arguments)
{
this.arguments=arguments;
}
public Object[] getArguments()
{
return this.arguments;
}

}
class Whatever implements java.io.Serializable
{
public int x;
public int y;
public java.util.ArrayList list;
public String str;
public float f;
}
class eg3psp
{
public static void main(String gg[])
{
Gson gson=new Gson();
Response response=new Response();
response.setServicePath("/calculator/add");

response.setArguments(new Whatever());
Object []l=response.getArguments();
for(Object i:l)
{
System.out.println(i+" Type : "+i.getClass().getName());
}
String json=gson.toJson(response);
System.out.println(json);
Response res=gson.fromJson(json,Response.class);

System.out.println(res.getServicePath());
Object []args=res.getArguments();
//args[0]=gson.fromJson(gson.toJson(args[0]),Whatever.class);
//System.out.println(w.getClass().getName());
for(Object i:args)
{
System.out.println(i+" Type : "+i.getClass().getName());
}
}
}
