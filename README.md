##StaggeredGridView

Is a open source android library which provides GridView UI with uneven item heights. This library was inspired to write when problems were seen using other staggered gridview android libraries especially when showing different item types in gridview and adding items to the end of gridview brings to the top of the view plus and when an gridview item changes its content dynamically items overlaps with other items and few other issues.All these issues are addressed throught this simple implementation using ScrollView. Very simple, it doesn't use adapter and listview approach. But it has its own pros and cons.

![alt text](https://raw.github.com/smanikandan14/StaggeredGridView/master/Staggered1_SNAPSHOT.png "")

Used in the below Android apps.

* [Goinout](https://play.google.com/store/apps/details?id=com.edenpod.goinout&hl=en)

If you are using StaggeredGridView in your app and would like to be listed here, please drop me a mail.

## Setup
Add the 'library' project to your workspace then add it as a library project to your application project.

## Usage

Three steps are involved in initializing and using StaggeredGridView.

__1.__      Initialize StaggeredGridView.
#### XML Usage
```xml
<com.mani.view.StaggeredGridView
    android:id="@+id/staggeredview"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    staggered:columnCount="2"
    staggered:mode="dynamic"/>
```
* `columnCount` - (integer) number of columns to display in StaggeredGridView
* `mode` - (string) mode either "fixed" or "dynamic"

Mode.Fixed - If the item's height are known(fixed) Use this mode. Items are arranged in the order in which it is added to the grid view.

Mode.Dynamic -  Order is not maintained. If the item content changes dynamically for example, a image is downloaded 
and the size is unknown. Use this mode, so that item positions are re-calculated when a change in height for an item is detected and height between columns are maintained close to equal.

#### Instance creation

```
mStaggeredView = new StaggeredGridView(this);
// Be sure before calling initialize that you haven't initialised from XML 
mStaggeredView.initialize(2, StaggeredGridView.Mode.FIXED);
mStaggeredView.setOnScrollListener(scrollListener);
``` 

NOTE : **UnsupportedOperationException** would be thrown if initialize method is called twice.Be sure before calling initialize that you haven't initialised from XML 

__2.__       Add StaggeredGridViewItems to StaggeredGridView    
There are two ways you can add items to StaggeredGridView.
* mStaggeredView.addItem(item);
* mStaggeredView.addItemsList(items);

Either you can add one item at a time or add a list of gridview items. `List<StaggeredGridViewItems>`

__3.__       StaggeredGridViewItem   
All the gridview items should implement this abstract class. It has two methods.
* View getView(LayoutInflater inflater, ViewGroup parent); Return the 'view' for the gridviewitem.
* int getViewHeight(LayoutInflater inflater, ViewGroup parent); Return the height of the gridviewitem

### onScrollListener
Register for onScroll events through **setOnScrollListener()** method.

``` java
	private OnScrollListener scrollListener = new OnScrollListener() {
		public void onTop() {
		}
		
		public void onScroll() {

		}

		public void onBottom() {
			loadMoreData();
		}
	};
```

## Demo Example.
Demo uses Flickr api to list down different items in the gridview. Each item is different and explained in the below picture.

Each item has it own onClickListener implemented. Since each item is written in a new class, it is easy to maintain the business logic for each item in a single class. 

![alt text](https://raw.github.com/smanikandan14/StaggeredGridView/master/Staggered3_SNAPSHOT.png "")

## Cons
- Items are not recycled as in the listview/adapter model, all items are kept in memory just like in scrollview. So memory might be an issue.
- Cannot add or delete items in the middle. No refreshing the data. Once added cannot be removed. But most of the situations where StaggeredGridView is used, this situation shouldnt occur. Ex Pinterest,Goinout,Carousell etc. Most of the products just allow users to load more, doesnt provide users to delete.


##License
```
 Copyright 2013 Mani Selvaraj
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
```
  
  
