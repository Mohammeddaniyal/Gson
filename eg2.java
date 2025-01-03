import com.google.gson.*;
class eg2psp
{
public static void main(String gg[])
{
Gson gson=new Gson();
int i=10;
String json=gson.toJson(i);
System.out.println(json);
int f=gson.fromJson(json,int.class);
System.out.println(f);
}
}
