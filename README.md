本田案件デモアプリ
Note 
1. Add arrow back button on Action bar
- AndroidManifest.xml 
<activity android:name=".ControllerActivity"
android:parentActivityName=".MainActivity"/>

- Java file
getSupportActionBar().setDisplayHomeAsUpEnabled(true);
