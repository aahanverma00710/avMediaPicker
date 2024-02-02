## How to import into Project

### Step 1. Add the JitPack repository to your build file
```groovy
  allprojects {
	  	repositories {
			  maven { url 'https://jitpack.io' }
		  }
  	}
```
### Step 2. Add the dependency
```groovy
  	dependencies {
           implementation 'com.github.aahanverma00710:avMediaPicker:Tag'
  	}
```

### Usage In Project

### Create a MediaSelection instance for managing media choices.
```koltin
        data class MediaSelectionOptions(
            var mediaMode: MediaMode = MediaMode.All, // Selection of media type
                        //  All (Both Images & videos) , Pictures (Only Images), Video(for video only)
            val selectionCount: Int = 1, // No of items to be selected
            var preSelectedUrls: ArrayList<Uri> = ArrayList() // For showing media pre-selected
        )
```

### Launch the Selection Fragment within the designated container.
```koltin
        selectMedia(R.id.tv,options){
                  when(it.status){
                      AvMediaEventCallback.Status.SUCCESS ->{
                          val uriList = it.data
                          Log.e("selectedMediaList",uriList.size.toString())
                          removeFragment()
                      }
                      AvMediaEventCallback.Status.BACK_PRESSED -> {
                          removeFragment()
                      }
                  }
              }
```

## Updates

### Update V1.1.0

- Added documentation for the latest changes.
- Check out the [Release Notes](https://github.com/aahanverma00710/avMediaPicker/releases/tag/v1.1.0) for more details.
