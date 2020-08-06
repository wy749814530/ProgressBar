# ProgressBar 
自定义线性带文字提示的进度条

## 布局属性
``` java
    <com.mcustom.library.LineProgressbar
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












