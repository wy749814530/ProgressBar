# ProgressBar 
自定义线性带文字提示的进度条

## 布局属性
``` java
    <com.mcustom.library.Progressbar
        android:id="@+id/progressbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginLeft="20dp"
        android:layout_marginTop="50dp"
        android:layout_marginRight="20dp"
        apps:defaultProgress="15"
        apps:innerPointRadius="5"
        apps:maxProgress="25"
        apps:minProgress="1"
        apps:outerPointRadius="10"
        apps:progressHeight="3dp"
        apps:progressSpendColor="#25d1d3"
        apps:progressbgColor="#eeeeee"
        apps:relativesite="top_move"
        apps:textPointSize="18dp"
        apps:textUnit=" fps" />
```

## 方法

### 一、 进度条设置
#### 1. 设置进度条最小值
```java
setMinProgress(1)
```

#### 2. 设置进度条最大值
```java
setMaxProgress(70)
```

#### 3. 设置当前进度
```java
setProgress(25)
```
#### 4. 设置进度条背景颜色
```java
setProgressBgColor(R.color.gray)
```

#### 5. 设置进度条前景进度颜色
```java
setProgressSpendColor(R.color.blue)
```

### 二、 拖动按钮设置
#### 1. 拖动按钮为图片(当设置了此属性，则不会在显示默认的圆形拖拽按钮,默认圆形按钮颜色与进度条前景色相同)
```java
setPointImage(R.mipmap.drag_point)
```

#### 2. 默认的圆形拖拽按钮设置
```java 
/**
* 设置进度拖动按钮内圈半径
*
* @param radius 
* @return
*/
setInnerRadius(8)

/**
* 设置进度拖动按钮外心圆半径
*
* @param radius
* @return
*/
setOuterRadius(15)
```

### 二、 进度提示文字设置

#### 1. 设置提示进度文字布局位置
```java
enum SITE {
    TOP,      // 在进度条中间上方显示
    TOP_MOVE, // 在进度条上方跟随进度一起移动位置
    GONE      // 不显示进度条
}
setRelativeSite(SITE site)
```

#### 2. 设置提示进度文字字号
```java
setTextSize(16)
```

#### 3. 设置提示进度文字的单位
```java
setUnit("kg")

```
#### 4. 设置提示进度文字颜色
```java
setTextColor(R.color.red)
```




# RoundProgressStatusBar 
自定义带有成功与失败状态的圆形进度控件
<center>
<figure>
    <img src="https://img-blog.csdnimg.cn/20200806152357564.jpg" alt="图片替换文本" width="160" height="355" align="left" />
    <img src="https://img-blog.csdnimg.cn/20200806151031320.jpg" alt="图片替换文本" width="160" height="355" align="cehter" />
    <img src="https://img-blog.csdnimg.cn/20200806151031279.jpg" alt="图片替换文本" width="160" height="355" align="right" />
</figure>
</center>

## 如何使用
在工程build.gradle中对应添加下边一行代码
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
在App build.gradle 中添加依赖
```java
dependencies {
   implementation 'com.github.wy749814530:ProgressBar:latest.release' 
   //或者 implementation 'com.github.wy749814530:ProgressBar:1.0.6' 
}
```

## 布局属性
```java
    <com.mcustom.library.RoundProgressStatusBar
        android:id="@+id/roundProgressBar"
        android:layout_width="90dip"
        android:layout_height="90dip"
        android:layout_centerHorizontal="true"
        android:layout_marginLeft="20dp"
        android:layout_marginTop="40dp"
        android:textColor="#333333"
        android:textSize="15sp"
        app:failedColor="#FFFFFF"
        app:percentColor="#696969"
        app:percentSize="9sp"
        app:roundColor="#D1D1D1"
        app:roundProgressColor="#25d1d3"
        app:roundWidth="5dip"
        app:statusStrokeWidth="5dp"
        app:textIsDisplayable="true" />
```

## 属性
<declare-styleable name="RoundProgressStatusBar">
		<!-- 外边框背景色-->
        <attr name="roundColor" format="color" />
        <!-- 外边框进度前景色-->
        <attr name="roundProgressColor" format="color" />
        <!-- 外边框宽度-->
        <attr name="roundWidth" format="dimension"></attr>
        <!-- 成功或者失败时，跳动背景色-->
        <attr name="heartbeatColor" format="color" />
        <!-- 成功线条色-->
        <attr name="successColor" format="color" />
        <!-- 失败线条色-->
        <attr name="failedColor" format="color" />
         <!-- 成功或者失败线条宽度-->
        <attr name="statusStrokeWidth" format="dimension"></attr>
        <!-- 进度文字颜色-->
        <attr name="android:textColor" />
        <!-- 进度文字字号-->
        <attr name="android:textSize" />
		<!-- 进度%颜色-->
        <attr name="percentColor" format="reference|color" />
        <!-- 进度%字号-->
        <attr name="percentSize" format="dimension" />
        <!-- 进度最大值-->
        <attr name="max" format="integer"></attr>
        <!-- 是否显示文字-->
        <attr name="textIsDisplayable" format="boolean"></attr>
    </declare-styleable>
    

## 主要方法
```java 
// 设置进度
setProgress(progress)
// 加载成功动画
loadSuccess()
// 加载失败动画
loadFailure()
```







