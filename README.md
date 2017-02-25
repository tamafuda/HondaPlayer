本田案件デモアプリ
Note   
1.Add arrow back button on Action bar  
- AndroidManifest.xml 
<activity android:name=".ControllerActivity"
android:parentActivityName=".MainActivity"/>
- Java file
getSupportActionBar().setDisplayHomeAsUpEnabled(true);

2.Fix bug : Not a primitive field
https://github.com/LukeDeighton/WheelView/issues/31