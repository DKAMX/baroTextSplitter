[English](README.md)

# 潜渊症 文本分离器

一个用于分离XML属性的小程序。例如物品元素有`name`（名称）和`description`（描述）两种属性。  

下面是一个典型的程序如何工作的例子：  
**输入**  

```
<Items>
   <Item name="" identifier="guitar">
</Items>
```

**输出**  

```
<infotexts language="English" nowhitespace="false" translatedname="English">
   <entityname.guitar> </entityname.guitar>
   <entitydescription.guitar> </entitydescription.guitar>
</infotexts>
```

注意到程序会复制例如物品XML文件里的属性，然后生成对应的文本XML元素。这样就省去了手打XML元素的劳动，只需要专注于编写对应游戏物体实际的文本。  

目前，这个程序只能处理和`Affliction`（减益）或`Item`（物品）相关的元素。  

## 如何使用

[**下载**](https://github.com/DKAMX/baroTextSplitter/releases)压缩包。  

1. 安装JDK（JDK11或更高版本）  
   你可以执行指令：`java -version`来检查你的Java版本。  
2. 从压缩包里解压出文件，所以文件放在同一个文件夹内。  
3. 双击[`RUN_TextSplitter.bat`](RUN_TextSplitter.bat)脚本来启动程序。  
