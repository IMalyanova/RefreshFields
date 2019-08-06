public class Loader {

    public static void main(String[] args)   {

        RefreshFields refreshFields = RefreshFields.getInstance();
        System.out.println(refreshFields.getAddress().getStreet());

   }

}
