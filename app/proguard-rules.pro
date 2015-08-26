# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\lixiangos0170\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 哪些内容需要手动配置
#（1） 只在 AndroidManifest.xml 引用的类
#（2） 通过JNI回调方式被调用的函数
#（3） 运行时动态调用的函数或者成员变量
#（4） 自定义View，需要在xml引用到的
#（5） 我们用到反射的地方
#（6） 枚举
#（7） 注解
#
#具体保护某个自定义控件
#-keep public class com.viewpagerindicator.TitlePageIndicator
#
#不混淆某个包所有的类或指定的类
#
#例如，不混淆package com.ticktick.example下的所有类/接口
#-keep class com.ticktick.example.** { * ; }
#
#例如，不混淆com.ticktick.example.Test类
#-keep class com.ticktick.example.Test { * ; }
#
#不混淆某个类的特定的函数
#例如：不混淆com.ticktick.example.Test类的setTestString函数
#
#-keepclassmembers class
#com.ticktick.example.Test {
#    public void setTestString(java.lang.String);
#}
#
#不混淆某个类的构造函数
#例如：不混淆Test类的构造函数：
#
#-keepclassmembers class
#com.ticktick.example.Test {
#    public <init>(int,int);
#}
#
#-keepclassmembers 指定的类成员被保留。
#
#
##保持 native 方法不被混淆
# -keepclasseswithmembernames class * {
#     native <methods>;
# }
# #保持自定义控件类不被混淆
# -keepclasseswithmembers class * {
#     public <init>(android.content.Context, android.util.AttributeSet);
# }
# #保持自定义控件类不被混淆
# -keepclasseswithmembers class * {
#     public <init>(android.content.Context, android.util.AttributeSet, int);
# }
# #保持自定义控件类不被混淆
# -keepclassmembers class * extends android.app.Activity {
#    public void *(android.view.View);
# }
# #保持 Parcelable 不被混淆
# -keep class * implements android.os.Parcelable {
#   public static final android.os.Parcelable$Creator *;
# }
# #保持 Serializable 不被混淆
# -keepnames class * implements java.io.Serializable
# #保持 Serializable 不被混淆并且enum 类也不被混淆
# -keepclassmembers class * implements java.io.Serializable {
#     static final long serialVersionUID;
#     private static final java.io.ObjectStreamField[] serialPersistentFields;
#     !static !transient <fields>;
#     !private <fields><span></span>;
#     !private <methods>;
#     private void writeObject(java.io.ObjectOutputStream);
#     private void readObject(java.io.ObjectInputStream);
#     java.lang.Object writeReplace();
#     java.lang.Object readResolve();
# }
#
#
#
#
#
#
#
#
#
#
#
#
#
#
#
#
#
#
#

#保护view的子类不被混淆，因为自定义控件都是继承view
-keep public class * extends android.app.Dialog
-keep public class * extends android.view

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#忽略警告
-ignorewarning

#缺省proguard 会检查每一个引用是否正确，但是第三方库里面往往有些不会用到的类，没有正确引用。如果不配置的话，系统就会报错。
-dontwarn android.support.**

##################-dontwarn和-keep 结合使用##################
##################-keep  com.xx.bbb.**{*;}  这个包里面的所有类和所有方法而不混淆，##################
##################-dontwarn  com.xx.bbb**  来屏蔽警告信息##################

-dontwarn com.juyou.**
-dontwarn com.gotye.api.**
-dontwarn ads.**
-dontwarn com.iflytek.**
-dontwarn com.iflytek.sunflower.**

-keep public class com.juyou.**{*;}
-keep public class com.gotye.api.**{*;}
-keep public class ads.**{*;}
-keep public class com.iflytek.**{*;}
-keep public class com.iflytek.sunflower.**{*;}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}


-keepclasseswithmembernames class * { ##【对所有类的native方法名不进行混淆】
native <methods>;
}