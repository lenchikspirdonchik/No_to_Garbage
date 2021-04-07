package spiridonov.no_to_garbage


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*
import spiridonov.no_to_garbage.descriptionMenu.OnethingActivity
import spiridonov.no_to_garbage.homeMenu.AddGarbageActivity
import spiridonov.no_to_garbage.homeMenu.AllMapsActivity
import spiridonov.no_to_garbage.homeMenu.StatisticsActivity
import spiridonov.no_to_garbage.mainMenu.AboutAppActivity
import spiridonov.no_to_garbage.mainMenu.AccountActivity
import spiridonov.no_to_garbage.mainMenu.AddImageActivity
import spiridonov.no_to_garbage.mainMenu.AddMapActivity


class MainActivity : AppCompatActivity() {
    private var mRewardedAd: RewardedAd? = null
    private var screenWidth = 0
    private var screenHeight = 0
    val TLP = LinearLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    )
    val TRP = TableRow.LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT
    )
    val LP = TableLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    )
    val imageP = TableLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    )
    val linearimageP = LinearLayout.LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        loadAd(adRequest)


        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        screenWidth = displaymetrics.widthPixels
        screenHeight = displaymetrics.heightPixels
        val category = arrayOf("Кухня", "Ванная", "Гардеробная", "Кабинет")
        val layout = findViewById<LinearLayout>(R.id.linearMain)
        imageP.setMargins(15)
        linearimageP.setMargins(15)
        val table = TableLayout(this)
        table.layoutParams = TLP
        layout.addView(table)

        for (name in category) {
            val trName = TableRow(this)
            val trPhoto = TableRow(this)
            trPhoto.layoutParams = LP
            val textCategory = TextView(this)
            textCategory.setTextColor(Color.BLACK)
            textCategory.textSize = 22F
            table.layoutParams = TLP

            textCategory.text = " ${name}"
            trName.addView(textCategory)
            val horizontalScrollView = HorizontalScrollView(this)
            horizontalScrollView.layoutParams = TRP

            val linearscroll = LinearLayout(this)
            linearscroll.orientation = LinearLayout.HORIZONTAL
            val view = View(this)
            view.layoutParams = TLP
            view.setBackgroundColor(Color.GRAY)

            horizontalScrollView.addView(linearscroll)
            trPhoto.addView(horizontalScrollView)
            val photocategory = setCategory(name)
            val namecategory = setRusCategory(name)
            for (i in 0..photocategory.lastIndex) {
                val resID =
                    resources.getIdentifier(photocategory[i], "drawable", packageName)
                val linearImage = LinearLayout(this)
                linearImage.orientation = LinearLayout.VERTICAL
                linearImage.layoutParams = linearimageP

                val imageView = ImageView(this)
                val bMap = BitmapFactory.decodeResource(resources, resID)
                val bMapScaled = Bitmap.createScaledBitmap(
                    bMap,
                    screenWidth / 3 + 20,
                    screenHeight / 5 + 15,
                    true
                )
                imageView.setImageBitmap(bMapScaled)
                imageView.layoutParams = TLP
                val text = TextView(this)
                text.layoutParams = TLP
                text.text = namecategory[i]
                imageView.setOnClickListener {
                    it
                    val mintent = Intent(this, OnethingActivity::class.java)
                    val msp = getSharedPreferences("things", Context.MODE_PRIVATE)
                    val editor = msp?.edit()
                    if (editor != null) {
                        editor.putString("thing", namecategory[i])
                        editor.apply()
                        startActivity(mintent)
                    }

                }
                linearImage.addView(imageView)
                linearImage.addView(text)
                linearscroll.addView(linearImage)
            }

            table.addView(trName)
            table.addView(trPhoto)

        }

        val trName = TableRow(this)
        val trPhoto = TableRow(this)
        trPhoto.layoutParams = LP
        val textCategory = TextView(this)
        textCategory.setTextColor(Color.BLACK)
        textCategory.textSize = 22F
        table.layoutParams = TLP
        textCategory.text = " Другое"
        trName.addView(textCategory)
        val horizontalScrollView = HorizontalScrollView(this)
        horizontalScrollView.layoutParams = TRP
        val linearscroll = LinearLayout(this)
        linearscroll.orientation = LinearLayout.HORIZONTAL
        trPhoto.addView(horizontalScrollView)
        horizontalScrollView.addView(linearscroll)
        addcategory(
            linearscroll = linearscroll,
            name = "Все мусорки города",
            photo = "all_garbage",
            intent = Intent(this, AllMapsActivity::class.java)
        )
        addcategory(
            linearscroll = linearscroll,
            name = "Выкинуть мусор",
            photo = "add_garbage",
            intent = Intent(this, AddGarbageActivity::class.java)
        )
        addcategory(
            linearscroll = linearscroll,
            name = "Статистика",
            photo = "statistics",
            intent = Intent(this, StatisticsActivity::class.java)
        )
        addcategory(
            linearscroll = linearscroll,
            name = "Добавить фото мусора",
            photo = "add_garbage",
            intent = Intent(this, AddImageActivity::class.java)
        )
        addcategory(
            linearscroll = linearscroll,
            name = "Добавить мусорку",
            photo = "add_garbage",
            intent = Intent(this, AddMapActivity::class.java)
        )
        table.addView(trName)
        table.addView(trPhoto)
        TLP.setMargins(15)

        val btnAbout = Button(this)
        btnAbout.layoutParams = TLP
        btnAbout.text = " О приложении"
        btnAbout.setOnClickListener {
            mRewardedAd?.show(parent, OnUserEarnedRewardListener {
                var rewardAmount = it.amount
                var rewardType = it.type
                Log.d("TAG", "User earned the reward.")
                loadAd(adRequest)
            })
            startActivity(Intent(this, AboutAppActivity::class.java))
        }
        btnAbout.background = getDrawable(R.drawable.button)

        val btnAccount = Button(this)
        btnAccount.layoutParams = TLP
        btnAccount.text = " Личный кабинет"
        btnAccount.setOnClickListener {
            mRewardedAd?.show(parent, OnUserEarnedRewardListener {
                var rewardAmount = it.amount
                var rewardType = it.type
                Log.d("TAG", "User earned the reward.")
                loadAd(adRequest)
            })
            startActivity(Intent(this, AccountActivity::class.java))
        }
        btnAccount.background = getDrawable(R.drawable.button)
        layout.addView(btnAccount)
        layout.addView(btnAbout)


    }

    private fun addcategory(
        linearscroll: LinearLayout,
        photo: String,
        name: String,
        intent: Intent
    ) {
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        screenWidth = displaymetrics.widthPixels
        screenHeight = displaymetrics.heightPixels
        val resID = resources.getIdentifier(photo, "drawable", packageName)
        val linearImage = LinearLayout(this)
        linearImage.orientation = LinearLayout.VERTICAL
        linearImage.layoutParams = linearimageP
        val imageView = ImageView(this)
        val bMap = BitmapFactory.decodeResource(resources, resID)
        Log.d("screenWidth", screenWidth.toString())
        Log.d("screenHeight", screenHeight.toString())
        if (bMap != null) {
            val bMapScaled =
                Bitmap.createScaledBitmap(bMap, screenWidth / 3 + 20, screenHeight / 5 + 15, true)
            imageView.setImageBitmap(bMapScaled)
            imageView.layoutParams = TLP
            imageView.setOnClickListener {

                mRewardedAd?.show(this, OnUserEarnedRewardListener {
                    var rewardAmount = it.amount
                    var rewardType = it.type
                    Log.d("TAG", "User earned the reward.")
                    val adRequest = AdRequest.Builder().build()
                    loadAd(adRequest)
                })
                startActivity(intent)
            }
        }
        val text = TextView(this)
        text.layoutParams = TLP
        text.text = name
        linearImage.addView(imageView)
        linearImage.addView(text)
        linearscroll.addView(linearImage)
    }

    private fun setRusCategory(mainCategory: String): Array<String> {
        return when (mainCategory) {
            "Кухня" -> arrayOf("Стеклянные банки", "Бутылки", "Контейнеры", "Коробки")
            "Ванная" -> arrayOf("Бутылки")
            "Гардеробная" -> arrayOf("Одежда в плохом состоянии", "Одежда в хорошем состоянии")
            "Кабинет" -> arrayOf("Батарейки", "Бумага", "Техника")
            else -> arrayOf("Батарейки", "Бумага", "Техника")
        }
    }

    private fun setCategory(mainCategory: String): Array<String> {
        return when (mainCategory) {
            "Кухня" -> arrayOf("jars", "kitchenbottles", "containers", "box")
            "Ванная" -> arrayOf("bathbottles")
            "Гардеробная" -> arrayOf("badclothes", "goodclothes")
            "Кабинет" -> arrayOf("battery", "paper", "technic")
            else -> arrayOf("battery", "paper", "technic")
        }
    }

    private fun loadAd(adRequest: AdRequest) {
        RewardedAd.load(
            this,
            resources.getString(R.string.adVideoId),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("TAG", adError.message)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d("TAG", "Ad was loaded.")
                    mRewardedAd = rewardedAd
                }
            })
    }


}/*val photo = arrayOf(
            "Батарейки" to "battery",
            "Бумага" to "paper",
            "Техника" to "technic",
            "Бутылки" to "kitchenbottles",
            "Бутылки " to "bathbottles",
            "Одежда в плохом состоянии" to "badclothes",
            "Одежда в хорошем состоянии" to "goodclothes",
            "Стеклянные банки" to "jars",
            "Контейнеры" to "containers",
            "Коробки" to "box"
        )*/