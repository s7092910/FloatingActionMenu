# FloatingActionMenu

Yet another library for drawing Material Design promoted actions. The difference is this library extending Google's Design Library

##Features

* `FloatingActionMenu` which can be expanded/collapsed to reveal multiple actions.
* Support for normal `56dp` and mini `40dp` buttons in the `FloatingActionMenu`.
* Optional labels for buttons in `FloatingActionMenu`.
* Support for `CardView` in the optional labels.

##Include in your project
There is currently an issue with jcenter. So to include this library into your project you have to add the maven 
repository to your `build.gradle`:
```
repositories {
    maven {
        url 'https://dl.bintray.com/s7092910/maven/'
    }
}
```

Add a dependency to your `build.gradle`:
```
dependencies {
    compile 'com.wanderingcan.widget:floatingactionmenu:1.1.0'
}
```

##Change Log
**1.1.1**
  * Bug fix for the newly added methods relating to Button Margins in the 'FloatingActionMenu'
  * 'setButtonMargin' and 'setButtonMarginResource' now calls 'requestLayout' so you don't have to.

**1.1.0**
  * Refactored LabelView to no longer inflate the view from xml and to only contain the view(s) required instead of 
  having all the views for both types of LabelView being in the view
  * Fixed a bug with expanding the menu to the left or right, the `FloatingActionMenu` expand to the left or right 
  will start from the closest button to the menu button instead of the farthest
  * Removed the xml that was required for LabelView

##Usage
### Floating action button
With the extension of Google's Design Library Floating Action Button, the Floating Action Button
behaves very much like the Floating Action Button in the Design Library with a few changes. These
changes allow custom animations, getting the label and setting/getting the text of the label.

Here is an example of **Floating Action Button**'s xml attributes:
```XML
<com.wanderingcan.widget.floatingactionmenu.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/fab"
            android:src="@drawable/ic_edit_white"
            app:fabSize="normal"
            app:elevation="6dp"
            app:pressedTranslationZ="12dp"
            app:label="Sample Text"
            app:backgroundTint="@android:color/holo_blue_bright"/>
```

You can set an icon for the Floating Action Button using `android:src` xml attribute. Use drawables of
size `24dp` as specified by
[guidlines](http://www.google.com/design/spec/components/buttons.html#buttons-floating-action-button).

The Floating Action Button allows you to set what text will be shown in the label using `app:label`
xml attribute.

Here is an explanation of the Floating Action Button xml attributes:

Attribute | Description
------------ | -------------
`app:android:background="reference"`| Background Drawable
`app:backgroundTint="color || reference"`| Background color of the FAB
`app:backgroundTintMode`| Background color of the FAB
`app:rippleColor="color || reference"`| Ripple color for the FAB
`app:fabSize="normal || mini"`| Size for the FAB
`app:elevation="dimension || reference"`| Elevation value for the FAB
`app:pressedTranslationZ="dimension || reference"`| TranslationZ value for the FAB when pressed
`app:label="String || reference"`| Text to be displayed by the Label when the Menu is open

### Floating action Menu
The Floating Action Menu xml attributes also has the xml attributes that set the attributes of the
main Floating Action Button.

Here is an example of **Floating Action Button**'s xml attributes:
```XML
 <com.wanderingcan.widget.floatingactionmenu.FloatingActionMenu
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/fam"
        app:menuBackgroundColor="#ccffffff"
        app:menuBackgroundFillParent="true"
        app:fabSize="normal"
        app:elevation="6dp"
        app:layout_expand="up">
        <com.wanderingcan.widget.floatingactionmenu.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/fab"
            android:src="@drawable/ic_edit_white"
            app:fabSize="mini"
            app:elevation="6dp"
            app:label="Fab" />
        <com.wanderingcan.widget.floatingactionmenu.FloatingActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/fab_2"
            android:src="@drawable/ic_edit_white"
            app:fabSize="mini"
            app:elevation="6dp"
            app:label="Fab 2" />
    </com.wanderingcan.widget.floatingactionmenu.FloatingActionMenu>
```
The Floating Action Menu will automatically create the labels and display them when the menu is open
if the following xml attribute are set:

 * `app:label` in the Floating Action Button tags
 * `app:labelType` in the Floating Action Menu tag
 * `app:labelStyle` in the Floating Action Menu tag
 
`app:labelStyle` or `app:labelType` are optional if the other is set. While you can have both set in
xml.

Here is an explanation of the Floating Action Menu xml attributes:


Attribute | Description
------------ | -------------
`app:android:background="reference"`| Background Drawable
`app:backgroundTint="color || reference"`| Background color of the FAB
`app:backgroundTintMode`| Background color of the FAB
`app:rippleColor="color || reference"`| Ripple color for the FAB
`app:fabSize="normal || mini"`| Size for the FAB
`app:elevation="dimension || reference"`| Elevation value for the FAB
`app:pressedTranslationZ="dimension || reference"`| TranslationZ value for the FAB when pressed
`app:menuOpenAnimation="reference"`| The animation that is played for each item when the menu is opened
`app:menuCloseAnimation="reference"`| The animation that is played for each item when the menu is closed
`app:menuItemAnimationDelay="integer"`| The time in between each animation in milliseconds when opening or closing the menu
`app:menuItemAnimationTime="integer"`| The time it takes to play an animation in milliseconds
`app:content_padding="dimension || reference"`| The padding of the menu from the edge of the view
`app:menuBackgroundColor="color || reference"`| The color that the background will dim to when the menu is open
`app:menuBackgroundFillParent="true || false"`| Sets if the dim background will match the parent view or only the menu size
`app:closeOnOutsideTouch="true || false"`| Sets if touching outside outside of the menu will close the menu
`app:labelType="card_label || text_label"`| The type of label that will be used. Card_Label uses a textview inside a cardview. While text_label uses a textview
`app:labelStyle="reference"`| A reference to a style attribute that sets the style of the text in the label
`app:layout_labels="right || left"`| The side of the menu that the labels will appear on
`app:layout_expand="up || down || right || left"`| The direction that the menu will open


##Caveats

Unlike many other FloatingActionMenu libraries this library extends Google's Design Library Floating Action Button.
So if there are any issues with the FloatingActionButton in the Design Library they will appear in this library.

This library is `minSdkVersion=14` and if that changes, the version number will be increased, not decreased.

##License


    Copyright 2015 Christopher Beda

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
