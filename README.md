[中文](README.zh-cn.md)

# Barotrauma Text Splitter

A mini program used for splitting attributes from XML elements. For example, the `name` and `description` of an item.  

An example of how the program will work with given input and output:  
**Input**  

```
<Items>
   <Item name="" identifier="guitar">
</Items>
```

**Output**  

```
<infotexts language="English" nowhitespace="false" translatedname="English">
   <entityname.guitar> </entityname.guitar>
   <entitydescription.guitar> </entitydescription.guitar>
</infotexts>
```

Notice that the program copies the attribute and generates the language translation XML file for you. So you are saved from the labor work of copying and pasting all these tag names. Just focusing on filling the actual text of this game object.  

Currently, this program can only process XML files that relate to `Affliction` or `Item` element.  

## How to use

[**Download**](https://github.com/DKAMX/baroTextSplitter/releases) the zip file.  

1. Install JDK (version 11 or above)  
   You can check your Java version with command: `java -version`  
2. Extract files from the zip. Place those files in the same folder.  
3. Double click on the script [`RUN_TextSplitter.bat`](RUN_TextSplitter.bat) to run.  
