package com.example.sunsetprediction

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.slaviboy.graphics.RectD
import com.slaviboy.voronoi.Delaunay
import com.slaviboy.voronoi.Polygon
import com.slaviboy.voronoi.Voronoi
import kotlin.math.sqrt

/**
 * Simple view, that creates random points, and generates the voronoi diagram. It draw
 * each cells with color corresponding to the distance from the view center to each
 * cell center.
 */
class VoronoiView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var map : Bitmap
    private var numberOfRandomPoints: Int          // number of points that will be generated
    private var viewCenterX: Double                // the center of the view once it is calculated
    private var viewCenterY: Double                // the center of the view once it is calculated
    private var halfDiagonalWidth: Double          // half of the diagonal width
    private lateinit var delaunay: Delaunay        // delaunay object for center points
    private lateinit var voronoi: Voronoi                  // voronoi object for cells
    private var paint: Paint                       // paint object for the drawing
    private var isInit: Boolean                    // if view size is initialized, right before drawing
    private var gradientPicker: GradientPicker     // gradient picker, that will set color for each cell
    private var relaxationLoops = 0                // how many times to apply the llyod relaxation
    private var useDistantColor: Boolean = true    // use the gradient picker to generate different color depending how close the cells center is to the center of the canvas
    private val path: Path = Path()                // the path for the generated cells
    private lateinit var database : DatabaseReference
    private val ratings : ArrayList<Double> = arrayListOf(
            2.988187028865887,
            2.940336654575491,
            0.7646819702563407,
            3.9918939102388133,
            2.468914024309128,
            1.513170947567655,
            4.338920365446366,
            3.564719314989229,
            3.686815504901692,
            3.851690720132837,
            2.785761099501431,
            3.3630040110576336,
            3.032136625159173,
            2.5226134541914087,
            2.577676373409487,
            1.3270538393962297,
            3.8053561323729275,
            2.8695446076496407,
            1.5492717418334936,
            2.96721476135771,
            2.3769910525177016,
            4.436651661629348,
            3.5205902535269686,
            0.9944606331559895,
            3.8053561323729275,
            0.9944606331559895,
            2.4457687205853427,
            3.572196831748651,
            4.1670948657197355,
            3.684260727090496,
            3.526201928787822,
            4.1670948657197355,
            0.867420245623268,
            0.0,
            3.9548294687970182,
            2.3762154842907375,
            1.145318950805669,
            3.5396791042281315,
            2.678898125226911,
            1.1517087188431376,
            2.064335742887533,
            2.50937484869311,
            1.436473003236685,
            0.31044573076597437,
            3.5328348508179834,
            2.3762154842907375,
            3.169297492107062,
            3.5205902535269686,
            2.64995814961034,
            4.027345098018165,
            3.2352654858931618,
            3.6369856781501078,
            3.9796591454043337,
            3.1843735213114774,
            3.694583544177519,
            3.7072782420970833,
            3.3430028678352555,
            3.1319425447577407,
            0.9372078332045458,
            2.0875865129947604,
            2.666007442531816,
            2.50937484869311,
            2.912685054638164,
            4.063462567598803,
            3.130280876212189,
            3.1000381084091577,
            1.1026895382715616,
            3.3369199654514055,
            0.8259760427147423,
            2.3115232351311867,
            4.144528182132385,
            2.0527811448774202,
            1.6176086434627424,
            3.0678400104659858,
            3.9707132044098086,
            0.9014857213863424,
            3.5887649196233884,
            4.2723901112699245,
            0.9765339389559642,
            3.0748772009223164,
            4.0550968079753655,
            0.44400982033869674,
            2.1475357312799197,
            3.1396981772007444,
            3.2395599773736574,
            2.3653896174680127,
            1.7239204852960397,
            2.9329459192103187,
            4.046603558594558,
            2.8658822584190675,
            3.6493850701960326,
            3.781804675115174,
            3.729650269619245,
            3.63028140437455,
            2.6707270881041785,
            0.6420971471153937,
            3.535863513172861,
            2.103672013273072,
            4.063260470755696,
            1.398564099936663,
            2.8997775014739386,
            0.7884894035615921,
            1.6269880097710172,
            4.4927216436338275,
            1.5910737664372763,
            4.890068352580411,
            4.362685678633758,
            3.0748772009223164,
            3.079389783596667,
            4.218789271695063,
            3.3118064430971557,
            1.841236307573484,
            3.7554544888273265,
            3.0890481956857623,
            0.5492718481303211,
            3.0286189434193678,
            3.1000381084091577,
            0.6975976438670424,
            2.103672013273072,
            1.7754747123302248,
            2.6622947600925784,
            3.87737688342698,
            2.5537602848159713,
            0.5068960864006125,
            2.390165481306454,
            1.2506689392219723,
            3.0564807743984304,
            2.8733424604218376,
            1.2198778043561185,
            2.6791413124373613,
            4.081661381676989,
            2.577676373409487,
            1.1173102682682126,
            2.7781244198518467,
            3.1346050311510827,
            0.512712117312155,
            2.305526034564433,
            1.8869365025803289,
            0.26483455935208255,
            2.0544141298884515,
            0.7239071716184069,
            2.3653896174680127,
            4.223036892918223,
            3.5589923741061567,
            3.4876248170565165,
            2.522206038382615,
            2.7563842771419784,
            1.8347870133126678,
            2.926609004778946,
            3.618604498577403,
            0.5211268399117163,
            3.4216623540272173,
            2.9290135801903534,
            3.688457259400985,
            2.9423438687784005,
            2.9281628028057796,
            2.96721476135771,
            0.1723252276864756,
            0.9532408494243256,
            0.5953515228110069,
            2.6219008030565867,
            2.927804785956191,
            2.290726526591188,
            3.5195879076622845,
            1.0625204986788102,
            1.3000478016832848,
            2.707692524016572,
            4.051979387769604,
            2.435609368088215,
            1.7239204852960397,
            0.9827469885178698,
            0.9014857213863424,
            2.8658822584190675,
            3.391951759629709,
            2.727404224299699,
            4.184290503489273,
            1.6359583990358453,
            2.497156493431361,
            2.3407347991417806,
            0.4891538171961374,
            4.183407442595594,
            2.4963349518265434,
            4.085782443240019,
            1.877037477657071,
            2.018142596343591,
            2.650526206499552,
            3.9707132044098086,
            1.4298541655361556,
            1.3868604206261128,
            4.793420619681804,
            4.027345098018165,
            2.875627202769224,
            1.8137254928013928,
            0.685523652984015,
            3.2546651883789717,
            3.327079636307531,
            4.1670948657197355,
            3.5589923741061567,
            3.604573118053203,
            3.947699476227698,
            1.3344272517003108,
            0.4864087016283664,
            1.3640968221659924,
            2.6262371156951145,
            2.323195490442135,
            2.1329320087756534,
            2.566090716795117,
            1.3787963446859586,
            2.2933836815342796,
            1.5910737664372763,
            3.526201928787822,
            2.9873018233506703,
            0.21933313941248786,
            1.4920071163600013,
            0.8375465852489661,
            3.288972290117844,
            3.5743657520763623,
            3.4356737706785196,
            2.4926806329891034,
            2.8893481384263193,
            2.9549203737607717,
            3.8964434117943876,
            1.5832155077291878,
            1.837440580737835,
            3.1742655732957283,
            2.43962386708177,
            0.20786849509256777,
            4.338920365446366,
            3.1247804140070095,
            4.060311065972754,
            1.0477230502065957,
            0.9726456010094373,
            3.7533865504856516,
            2.406958386971155,
            3.167987400318157,
            3.3525880181578365,
            2.4906189395860387,
            2.910505811891979,
            1.3780905337519798,
            3.8649758969286436,
            2.4557238835205744,
            3.9707132044098086,
            2.631074152826244,
            3.127784362349515,
            2.875627202769224,
            4.338920365446366,
            3.0825890606639716,
            4.0243700491256105,
            1.2572840565335426,
            3.0907417449750962,
            3.6315951667257766,
            1.5982538513865119,
            2.0323968680277584,
            3.3369199654514055,
            4.74197096215158,
            2.551140333762471,
            2.875529052597105,
            2.675140930991196,
            2.8128957563755375,
            3.0206730728730657,
            2.5537602848159713,
            4.626958060639895,
            2.171788614727521,
            3.175310022669393,
            2.6159464866161395,
            1.620728056734018,
            1.3968149856416903,
            2.685001655838829,
            3.4356737706785196,
            3.5662713151048804,
            2.745304593426062,
            0.7887208649032696,
            2.988187028865887,
            3.001504350071434,
            4.0243700491256105,
            2.4127227974858885,
            3.3837673357502753,
            4.061431035920514,
            0.43811061215970865,
            4.218789271695063,
            3.7554544888273265,
            1.273364374841518,
            2.0943069306009803,
            3.6049405728984443,
            1.5429276814318096,
            1.7496097710594878,
            3.3369199654514055,
            1.0145909934591841,
            5.0,
            0.6085432248342129,
            4.235925781854355,
            3.2395599773736574,
            2.305526034564433,
            3.381450231001608,
            2.9968801411955375,
            2.910961098643084,
            4.436651661629348,
            1.2791766853641016,
            2.940774628646054,
            4.97749417134634,
            1.2194376026196216,
            1.318525113449275,
            2.232193979273288,
            2.6787867793002023,
            3.498292766655026,
            2.9885983041630038,
            4.471069378504241,
            0.5727506913678525,
            3.6103128145555647
        )

    companion object {

        /**
         * Inline function that is called, when the final measurement is made and
         * the view is about to be draw.
         */
        inline fun View.afterMeasured(crossinline f: View.() -> Unit) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        f()
                    }
                }
            })
        }
    }

    init {
        database = Firebase.database.reference
        isInit = false
        halfDiagonalWidth = 0.0
        numberOfRandomPoints = 100

        // create gradient picker that gets color on certain position
        gradientPicker = GradientPicker(
            arrayListOf(
                Color.parseColor("#495665"),
                Color.parseColor("#6d648d"),
                Color.parseColor("#b16697"),
                Color.parseColor("#ed6b79"),
                Color.parseColor("#ff8e42")
            ),
            arrayListOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)
        )

        paint = Paint().apply {
            isAntiAlias = true
        }
        viewCenterX = 0.0
        viewCenterY = 0.0

        // called on a final measure, when view size is available
        this.afterMeasured {
            paint.textSize = width / 25f

            isInit = true
            viewCenterX = width / 2.0
            viewCenterY = height / 2.0
            halfDiagonalWidth = Math.sqrt((width * width + height * height) / 4.0)



            initVoronoi()
        }
    }

    /**
     * Initialize the voronoi and delaunay objects by setting up random input points
     */
    private fun initVoronoi() {
        // generate random points
        Log.w("VORONOI", width.toString())
        val points = DoubleArray(numberOfRandomPoints * 2)
        for (i in 0 until numberOfRandomPoints) {
            points[i * 2] = (Math.random() * width - 1)
            points[i * 2 + 1] = (Math.random() * height - 1)
        }

        var coordinates1: DoubleArray = doubleArrayOf(
            397.6436951999999,304.31622226,
            774.7419445200001,328.11244451999994,
            549.6121770200001,386.69294274000003,
            974.3851183899999,187.03913911000006,
            735.18965476,329.42996798,
            736.47090226,285.88389660999997,
            1002.9119541099999,179.80609249999998,
            871.5531666100001,170.01107976000003,
            197.9055526999999,318.58947410999997,
            906.6406283900001,234.01817160999997,
            849.31140089,340.72301137,
            818.5602566099999,196.01479911,
            833.96686089,301.36181113,
            800.8379927399999,220.03425024,
            770.7430675,236.21557274000008,
            563.5198722599999,341.59906387,
            900.3587222599999,239.51458338999998,
            430.66096769999996,222.40706297999998,
            560.3676,340.76884048,
            793.06979089,182.39499887,
            401.5773048,359.28666661,
            949.1327704799999,208.22754726000005,
            847.4032500000001,456.76608339,
            590.25888887,396.02788363,
            893.2512347599999,237.94295702,
            589.22472226,390.33197226,
            742.9954691099999,224.80540411000004,
            821.30636113,381.60105548,
            955.7582654799999,209.12423113,
            252.41033390000007,286.63386113,
            197.06273519999993,322.97688613,
            954.1656441099999,206.7868679799999,
            642.18782548,309.6002661299999,
            599.8636054799999,231.94071024000004,
            827.62752774,416.70888887,
            718.20878452,189.6382974999999,
            680.39666661,304.27891661,
            845.06055548,461.51475,
            715.75808339,169.89722226000004,
            625.3123175000001,137.03070201999992,
            675.6135277399999,390.11238887,
            132.24781610000005,258.73805588999994,
            550.78752387,298.3174983900001,
            579.79905798,197.85718363,
            210.77969520000005,321.048,
            715.6241091100001,186.34688499999993,
            914.5792222599999,272.79161113,
            848.37489911,446.38624702,
            125.84250000000006,124.99172225999996,
            930.90060589,222.17680273999997,
            305.8630547999999,331.61916661,
            846.04119589,211.64508088999992,
            995.7202411300001,190.69201411000006,
            870.61297226,290.08013887000004,
            895.5656666099999,262.41216661,
            173.94290010000006,228.52420612999992,
            820.1621666100001,448.88516661,
            217.7758361,343.52954612999997,
            536.01377774,397.92719452,
            752.4879999999999,251.0353888699999,
            130.74167730000005,93.36730702,
            129.62766390000007,260.4769670200001,
            137.20684870000002,264.84834702,
            306.37785730000013,206.59740774,
            142.9569626999999,242.17783161,
            206.24021749999997,327.51380363,
            673.8801747599999,241.28932202,
            806.9347500000001,424.41697225999997,
            579.89989161,284.62767225999994,
            324.00252739999996,354.0265833899999,
            927.51317161,208.90983274000007,
            515.4077472600001,349.0075599999999,
            536.82883339,127.36605548,
            778.6939533900002,363.896245,
            1018.97691637,198.69811702000004,
            557.0811741100001,362.60809839,
            100.15347870000006,203.37204024000005,
            942.18816661,229.22108339,
            636.6745461299998,367.43468161,
            816.60356613,332.71076500000004,
            955.3493636300001,173.28196951999996,
            639.1941586300002,176.52961636999999,
            480.9992375999999,301.27066750000006,
            789.47514089,133.67285249999998,
            917.63044363,152.13732226000002,
            393.22963500000014,233.20628,
            705.1689154799999,147.62406297999996,
            806.7893054799999,297.58469451999997,
            922.6874525000001,197.2514595199999,
            755.6150224999999,182.00712499999997,
            186.0349423,297.62883297999997,
            918.34656863,182.4557927400001,
            1039.92163887,138.27344452,
            364.7713613000001,121.26973750000002,
            497.3057226000001,104.84011112999997,
            596.1267033899999,92.33978910999997,
            126.86202739999989,70.52325000000008,
            698.42880274,211.87424548,
            234.21277739999994,159.40444451999997,
            611.6481945200001,390.83583339,
            825.07280548,368.59652773999994,
            609.6671325000001,111.23641298000007,
            553.76036113,459.58383339,
            297.54317999999995,118.76848024000003,
            660.45621452,380.93626588999996,
            966.4446875,143.95893863000003,
            320.39887759999993,121.79267636999998,
            830.96782952,323.03972,
            825.47808548,204.42269702,
            287.32422500000007,259.08350524,
            97.97893389999992,189.73732726000003,
            761.53560774,304.40043839,
            876.30136113,251.64313886999992,
            138.40680730000008,223.4784927400001,
            650.91517226,187.96199838999996,
            773.96945024,113.73721702,
            216.26164099999994,336.8370729999999,
            571.8149891099999,379.99397952000004,
            709.27305548,219.33274999999992,
            705.68596589,98.13690725999993,
            344.1676537000001,241.16574613,
            356.5954663,143.15669910999998,
            430.09575000000007,240.30130548,
            642.26630411,240.09241797999994,
            400.1040763000001,170.55795548000003,
            552.47934024,427.90386588999996,
            822.91577476,247.65643589,
            766.0393611299999,347.22233339,
            685.66474952,138.78047411,
            428.19875229999997,200.35271749999993,
            832.01305548,403.94141661,
            778.270625,221.65961089000007,
            667.94415726,179.14968952000004,
            757.35630887,367.53723411,
            462.3668024000001,104.44438887000001,
            642.7079736300001,103.68446524000001,
            378.18592360000014,268.42421661000003,
            494.3050237,400.67919226000004,
            617.76840226,193.90613798000004,
            528.5507683899999,82.05783024000004,
            654.75377702,137.29062726000006,
            392.3991798999999,226.07634773999996,
            241.5411611999999,205.97625613000002,
            902.84546339,183.28146863000006,
            847.00433548,184.60563636999996,
            729.40656637,122.71461476000002,
            115.28234479999999,149.90458,
            721.97934661,253.35414636999997,
            900.26994452,303.75947226000005,
            820.0875554800001,482.54611113,
            564.1451888700001,102.37106886999993,
            174.79163870000002,274.80469452,
            869.0400000000001,305.14497226000003,
            267.66382020000003,78.70611975999998,
            311.5892888999999,302.64626976,
            854.69388887,318.84886112999993,
            786.3612413699999,169.58644339,
            583.71855452,237.83381701999997,
            565.3910952399999,159.11703113,
            605.75350113,299.27796523999996,
            761.6825113699999,203.36608089000003,
            415.8308561999999,146.06873297999994,
            497.68495010000004,255.23223547999999,
            211.92467749999992,90.46245548000002,
            558.0061111300001,84.86266499999999,
            599.90093,349.45736113,
            365.0455100999999,234.91898749999996,
            811.3797777400001,395.26905547999996,
            695.8084227400001,383.0762352400001,
            711.7969700000001,143.75375339000004,
            538.75361113,203.55297226000005,
            546.9274377400001,371.89673774000005,
            756.1129477400001,171.02606476000005,
            851.0659425,286.33830202,
            812.27940839,306.77365726000005,
            316.6983498999999,92.80596701999991,
            703.9467336299999,331.34431452,
            392.13591109999993,244.92262339,
            387.2997851,211.81993524000006,
            631.7371133900001,94.4278141100001,
            306.2932448999999,107.68409297999995,
            455.31052740000007,344.31202773999996,
            956.9712345199999,201.8617177399999,
            549.8753038700002,454.11539612999997,
            734.85713952,311.11239249999994,
            806.51352774,247.76666661000002,
            1015.23395089,191.62128137000002,
            522.3414286299999,239.63599161000002,
            553.6382208900001,259.95069887,
            304.80716609999996,160.25255548000007,
            924.6890277400001,225.46172226,
            885.6565554800001,317.39961113000004,
            712.0527775,121.08797910999999,
            622.1478666099999,74.37481725999999,
            245.16132479999993,341.8182552400001,
            448.0801060999999,80.97504363000007,
            967.2963970200001,206.48087750000002,
            910.2064445200001,177.65252773999998,
            206.89829260000005,293.79966798,
            327.45843870000004,158.67548911000006,
            678.70988887,350.71016661,
            603.52943839,268.41916863000006,
            532.4706802400001,102.19476952000002,
            772.01491298,172.76210000000003,
            413.5252312000001,197.69515,
            536.91717661,312.3488545199999,
            479.01277740000006,327.71813886999996,
            625.2021386299999,387.8563425,
            771.70838887,253.3714722599999,
            646.2098649999999,386.51024476,
            201.42261130000006,325.09872226000005,
            140.5354623,183.34555863000003,
            565.0927500000001,205.53347225999994,
            519.1537586299999,431.75644363000004,
            658.6388577399999,154.05054475999998,
            843.2089722599999,256.40880548000007,
            220.73829519999992,111.63353298000004,
            863.59268048,265.44635024,
            472.5674637000001,356.97702774000004,
            780.6459972600001,159.94044975999998,
            787.96341589,344.2215595199999,
            905.0221386300001,216.71058024,
            701.22950548,350.33673338999995,
            539.9436111300001,455.01083339,
            121.15053740000008,179.63812774000007,
            741.30240613,350.8890491100001,
            566.5958427400001,234.60355726,
            995.57005089,170.11322137000002,
            743.9502079800001,166.11870297999997,
            839.02130548,422.25325,
            671.37183411,195.37505137000005,
            645.3593077400001,347.31528548000006,
            189.75787259999993,260.3911736299999,
            709.8718333900001,378.24591661,
            153.77483619999998,260.3611072599999,
            488.23432369999995,79.59057773999996,
            724.2788630000001,111.98813699999994,
            138.66988739999994,278.02129750000006,
            691.26226863,166.62241773999995,
            270.46055479999995,102.42280548000008,
            375.80483390000006,245.34925726000006,
            1009.5563474999999,196.31856089000007,
            181.55677509999998,97.46896298000001,
            868.21833339,327.44425,
            890.59365774,307.90420452,
            988.11293952,181.45522952,
            97.81752740000012,161.90863887,
            183.47732120000012,318.58638589,
            700.8462499999998,269.96583338999994,
            753.3923054799999,386.39458339,
            909.6192125,268.7577754800001,
            685.21380548,208.70786339000006,
            691.2700177400001,365.05845952000004,
            804.3135295199999,425.51702338999996,
            295.8703613000001,170.50780548,
            505.1385949999999,145.49449201999994,
            768.455845,125.29423249999991,
            727.8134445200001,381.95380548,
            184.9765036,113.50043524,
            229.3936973000001,324.90333548,
            433.55827259999995,249.08551725999996,
            1014.7512499999999,158.01516661000005,
            458.0246964,151.22953637,
            131.0121676999999,211.34727863,
            150.45062390000007,147.68085725999993,
            695.34741113,182.67881387,
            689.0532886299999,124.26971798,
            355.89172239999994,192.8983025,
            850.41771839,265.46703838999997,
            889.5694738699999,166.97927613000002,
            432.9805548,333.87355548000005,
            637.53421726,153.54995136999992,
            406.50163869999994,294.51477774,
            829.56363887,353.83108339,
            172.71365390000005,314.75439701999994,
            742.58593911,190.94778863,
            159.0793172999999,300.97001298,
            886.612866,205.529498,
            622.4234129800001,266.84645613,
            278.9180548,269.46008339,
            868.76055298,249.51466339,
            614.9648283899999,348.40733250000005,
            501.5628048,366.91825000000006,
            162.22109749999998,306.71277839000004,
            685.4804836300001,222.65281702000004,
            535.63781339,322.19046613,
            806.5801111300001,434.27933339000003,
            611.0082766099999,125.72948911000003,
            266.9579648999999,160.41769636999993,
            571.4657620199998,179.15574339,
            950.2177483899999,194.43040863,
            916.19272048,167.1098220199999,
            375.55584000000005,254.78610362999996,
            776.04433339,383.25902773999996,
            785.2668333900001,193.02430547999995,
            809.0738504799999,279.92135911,
            938.1710383899999,215.29625112999997,
            700.9117849999999,317.44215839000003,
            755.1020050000001,139.39543976000004,
            263.7084948000001,177.80933386999993,
            612.1526599999999,331.28696298,
            588.1594361300001,349.9796388699999,
            782.12145089,291.18771726,
            348.33135489999995,212.50467160999995,
            794.29572226,376.6975,
            739.0681999999999,381.7847499999999,
            321.0001625,140.29721411000003,
            606.78421113,283.20821951999994,
            261.69847260000006,344.83808339000007
            )
        database.child("Coordinates").get().addOnSuccessListener { it ->
            Log.i("firebase", "Got value ${it.value}")
            val values : List<Any>? = it.value as? List<Any>
            Log.i("checker", "Got value ${values}")
            if (values != null) {
                coordinates1 = (values.mapNotNull{it as? Double }).toDoubleArray()
                Log.i("checker2", "Got value ${coordinates1.size}")
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        Log.i("checker2", "Got value ${coordinates1.size}")
        // generate delaunay and voronoi objects
        delaunay = Delaunay(*coordinates1)
        voronoi = Voronoi(delaunay, RectD(0.0, 0.0, width.toDouble(), 600.0))

        // apply relaxation to the points
        voronoi.relax(relaxationLoops)

        invalidate()
    }

    /**
     * Render all cells(polygon) for the voronoi diagram as paths, and also generate
     * the input point and the center of each cell as a circles.
     * @param canvas canvas where all triangles and points will be drawn
     * @param lineColor stroke color for all lines
     * @param centerCircleColor fill color for all circles
     * @param circleRadius radius for all circles
     */
    fun drawCellsWithPoints(
        canvas: Canvas, lineColor: Int = Color.BLACK, inputCircleColor: Int = Color.BLUE,
        centerCircleColor: Int = Color.GREEN, circleRadius: Double = canvas.width / 100.0
    ) {

        // render cells with different color depending on distance from the center
        for (i in 0 until delaunay.coordinates.size / 2) {
            path.reset()
            voronoi.renderCell(i, path)

            var cellColor = Color.WHITE
            if (useDistantColor) {
                // val cellCenterX = delaunay.coordinates[i * 2]                                   // center x of the voronoi shape
                // val cellCenterY = delaunay.coordinates[i * 2 + 1]                               // center x of the voronoi shape
                // val distance = distance(viewCenterX, viewCenterY, cellCenterX, cellCenterY)     // distance from the center screen to the cell center
                // val distanceInRange = (distance / halfDiagonalWidth).toFloat()                 // fit the distance in range between [0, 1]
                val rating = ratings[i] / 5
                cellColor = gradientPicker.getColorFromGradient(rating.toFloat())                // get the color corresponding to the distance
            }

            // fill the cell with the corresponding color
            paint.apply {
                color = cellColor
                style = Paint.Style.FILL
            }
            canvas.drawPath(path, paint)

            // stroke the cell
            paint.apply {
                color = lineColor
                style = Paint.Style.STROKE
                strokeWidth = 2.0f
            }
            // canvas.drawPath(path, paint)
        }

        // draw input points
        path.reset()
        paint.apply {
            color = inputCircleColor
            style = Paint.Style.FILL
        }
        // voronoi.renderInputPoints(circleRadius, path)
        canvas.drawPath(path, paint)

        // draw center points
        path.reset()
        paint.color = centerCircleColor
        // voronoi.renderCenters(circleRadius, path)
        canvas.drawPath(path, paint)
    }

    fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        val a = x1 - x2
        val b = y1 - y2
        return sqrt(a * a + b * b)
    }

    /**
     * Render all voronoi lines for the voronoi diagram.
     * @param canvas canvas where all lines will be drawn
     * @param lineColor stroke color for all lines
     */
    fun drawLines(canvas: Canvas, lineColor: Int = Color.BLACK) {

        // draw voronoi lines
        path.reset()
        paint.apply {
            color = lineColor
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
        }
        voronoi.render(path)
        canvas.drawPath(path, paint)
    }

    override fun onDraw(canvas: Canvas) {
        //drawLines(canvas)
        drawCellsWithPoints(canvas)
        val mapRect : Rect = Rect(0, 0, 1080, 600)
        map = BitmapFactory.decodeResource(resources, R.drawable.masked_us)
        canvas.drawBitmap(map, null, mapRect, paint)
    }
}