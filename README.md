# MVPMosbyDemo
Mosby官方地址:[Mosby](https://github.com/sockeqwe/mosby)
##使用
以MvpLceViewStateFragment为例，具有LCE和ViewState两种功能，简单的使用见[NewsListFragment.java](https://github.com/Blankeer/MVPMosbyDemo/blob/master/app/src/main/java/com/blanke/testmosby/lceviewstate/NewsListFragment.java)和[NewsListPersenter.java](https://github.com/Blankeer/MVPMosbyDemo/blob/master/app/src/main/java/com/blanke/testmosby/lceviewstate/persenter/NewsListPersenter.java);
简单的新闻列表，模拟耗时网络请求，首先页面后显示loading状态，然后显示新闻数据，如果错误可以在presenter中调用`getView().showError();`这是mosby实现的LCE效果。
然后旋转屏幕，看看会发生什么，效果图：
![viewstate_gif](https://raw.githubusercontent.com/Blankeer/MVPMosbyDemo/master/image/viewstate.gif)

可以看到旋转之后数据直接显示了，是没有再去获取数据的，说明viewstate是恢复了的，看log也能看出来。
##分析
先上uml，这是基础类的类图：
![base_class](https://raw.githubusercontent.com/Blankeer/MVPMosbyDemo/master/image/base_class.jpg)

MvpFragment是所有mvp\*Fragment的父类，它实现了BaseMvpDelegateCallback接口，从名字可以看出，它是一个代理类的回调，后面可以看到这个代理类就是FragmentMvpDelegate，可以看到FragmentMvpDelegate里面都是Fragment生命周期的声明，在MvpFragment中每个生命周期都是交给这个代理类处理。再说BaseMvpDelegateCallback，它是供FragmentMvpDelegate使用的，它实际上是view层必须实现的接口，官方对它的说明是`This interface must be implemented by all
 Fragment or android.view.View that you want to support mosbys mvp`,使用mosby必须在Fragment或View实现它，它里面的方法都是view层基本的方法，比如`createPresenter`，`getMvpView`等。到这里可以知道，当Fragment生命周期发生变化时，是交给FragmentMvpDelegate处理的，再看它的内部，它的构造方法需要传递delegateCallback对象也就是Fragment，内部又多了一个MvpInternalDelegate类，从名字可以看出它是内部处理的重要类，在Fragment回调onViewCreated生命周期时，有如下代码`getInternalDelegate().createPresenter(); getInternalDelegate().attachView();`MvpInternalDelegate会先创建Presenter，然后调用它的attachView()，MvpInternalDelegate的createPresenter方法:
 ```java
  void createPresenter() {
    P presenter = delegateCallback.getPresenter();
    if (presenter == null) {
      presenter = delegateCallback.createPresenter();
    }
    if (presenter == null) {
      throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
    }
    delegateCallback.setPresenter(presenter);
  }
 ```
它就是判断然后创建Presenter,这里会调用我们在Fragment实现的createPresenter(),presenter由我们自定义；注意，这里会判断是否为null，也就是在Fragment的onViewCreated的时候会检查createPresenter()是否为空，很容易忘写了报这个错。
attachView()方法:
```java
 void attachView() {
    getPresenter().attachView(delegateCallback.getMvpView());
  } 
```
调用了presenter的attachView方法，并将MvpView传递过去，看下MvpPresenter的基本实现类MvpBasePresenter：
```java
public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {
  private WeakReference<V> viewRef;
  @Override public void attachView(V view) {
    viewRef = new WeakReference<V>(view);
  }
  @Nullable public V getView() {
    return viewRef == null ? null : viewRef.get();
  }
  public boolean isViewAttached() {
    return viewRef != null && viewRef.get() != null;
  }
  @Override public void detachView(boolean retainInstance) {
    if (viewRef != null) {
      viewRef.clear();
      viewRef = null;
    }
  }
}
```
它内部使用了弱引用保存了MvpView，防止内存泄漏，attachView会初始化这个弱引用；它提供了一个有用的方法getView()，可以在presenter里获得mvpview对象，由于它是弱引用，注意要做非空判断。
分析到这里，基本的MvpFragment流程已经分析完了，整理一下，首先开发者需要继承MvpView设计需要操作UI的接口，比如showLoginSucc(),showLoginError(),再继承MvpBasePresenter，编写需要的功能逻辑，比如login(username,pwd)，这里一般是异步操作，需要在成功失败的回调里调用getView().showLoginSucc()和showLoginError(),最后再编写Fragment，需要继承MvpFragment,实现前面设计的MvpView接口，实现它的抽象方法createPresenter()返回presenter,实现showLoginSucc()/showLoginError()方法操作ui，设置button监听器，调用getPresenter().login(username,pwd)；。运行时的流程，当点击button时，会调用presenter的login方法，成功\失败调用fragment的showLoginSucc()\showLoginError()，修改ui。
上面的基本mvp搞明白了，后面的只是一些扩展。
###LCE
MvpLceFragment内置了R.id.loadingView/contentView/errorView，xml里需要显示设置这些id，必须得有，否则会报错，在onViewCreated()中会判断，；相应的MvpLceView内置了showLoading/showContent/showError/setData等方法，其中showloading/showError等方法，在MvpLceFragment里实现了默认的处理，动画显示和隐藏对应的控件。在使用时，只需要实现相应的方法即可，注意在presenter中设置数据和设置loading使用`getView().showLoading(false);getView().setData(newses);getView().showContent();`等方法。
###ViewState
这是viewstate的类图：
![viewstate_class](https://raw.githubusercontent.com/Blankeer/MVPMosbyDemo/master/image/viewstate_class.jpg)

继承自MvpViewStateFragment，需要设置setRetainInstance(true);
增加了ViewState类，用来保存和恢复view的状态数据。它只有一个方法`public void apply(V view, boolean retained);`用来恢复mvpview的状态，它的直接子类有LceViewState和RestorableViewState，前者是具有lce恢复功能，常用的是RetainingLceViewState，后者是具有parcelable保存恢复功能。他两的子类很多，其中AbsParcelableLceViewState实现了实现了这两个接口，一般常用的有：ArrayListLceViewState可以存放list，ParcelableDataLceViewState可以存放Parcelable对象，SerializeableLceViewState存放Serializable对象。
由于增加了ViewState，相应的BaseMvpDelegateCallback扩展成了BaseMvpViewStateDelegateCallback，增加了对ViewState的处理，FragmentMvpViewStateDelegateImpl扩展了FragmentMvpDelegateImpl，主要增加了对状态保存，fragment意外销毁的数据保存，比如旋转屏幕，对`onCreate\onActivityCreated\onSaveInstanceState`这3个方法进行处理：
```java
@Override public void onCreate(Bundle saved) {
    super.onCreate(saved);
    ((MvpViewStateInternalDelegate) getInternalDelegate()).createOrRestoreViewState(saved);
  }
  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ((MvpViewStateInternalDelegate) getInternalDelegate()).applyViewState();
  }
  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    ((MvpViewStateInternalDelegate) getInternalDelegate()).saveViewState(outState);
  }
```
调用MvpViewStateInternalDelegate的相应方法，创建/恢复/保存ViewState，在`createOrRestoreViewState`中首先判断用户是否返回了ViewState，根据是否需要Parcelable意外保存做了判断
```java
if (savedInstanceState != null
        && viewStateSupport.getViewState() instanceof RestorableViewState) {
      ViewState restoredViewState =
          ((RestorableViewState) viewStateSupport.getViewState()).restoreInstanceState(
              savedInstanceState);//从fragment oncreate参数savedInstanceState读取丢失的数据
      boolean restoredFromBundle = restoredViewState != null;
      if (restoredFromBundle) {
        viewStateSupport.setViewState(restoredViewState);
        applyViewState = true;
        return true;
      }
    }
```
接着在`onActivityCreated`中调用`applyViewState`恢复数据，`delegate.getViewState().apply(delegate.getMvpView(), retainingInstance);`交给相应的ViewState处理恢复。
在意外销毁时，`onSaveInstanceState`回调`saveViewState`，判断isRetainInstance(),state等再去保存数据。
```java
//省略为空判断
	boolean retainingInstanceState = delegate.isRetainInstance();
    if (viewState != null && !retainingInstanceState
        && !(viewState instanceof RestorableViewState)) {
      throw new IllegalStateException(
          "ViewState " + viewState.getClass().getSimpleName() + " of " + /*...省略报错信息*/);
    }
    // Save the viewstate
    if (viewState != null && viewState instanceof RestorableViewState) {
      ((RestorableViewState) viewState).saveInstanceState(outState);//保存到Bundle中
    }
```
####MvpLceViewStateFragment
它把LCE和ViewState合起来了，一般可以直接使用它。
###MvpFrameLayout/MvpLinearLayout/MvpRelativeLayout
mosby后来的版本才加对Layout的支持，这样意味这可以把功能模块缩小至Layout，以前是fragment+presenter，如果使用layout+presenter，相比fragment更灵活。举个例子，知乎的回答详情页面，回答详情区域、点赞、收藏都是单独的功能逻辑，而且都具有LCE特点，采用mvplayout的话实现更为方便。
Mvp\*Layout和Mvp\*Fragment原理类似，只不过view的生命周期和fragment不同。
目前V2.0.1，官方提供了mvpLayout、MvpViewState\*Layout的支持，未提供MvpLce\*Layout的支持，可能正在更新吧，可以自己扩展下。

update:自定义增加了[MvpLceViewStateFrameLayout.java](https://github.com/Blankeer/MVPMosbyDemo/blob/master/app/src/main/java/com/blanke/testmosby/MvpLceViewStateFrameLayout.java)

效果图：

![MvpLceViewStateFrameLayout_gif](https://raw.githubusercontent.com/Blankeer/MVPMosbyDemo/master/image/MvpLceViewStateFrameLayout.gif)







 
 
