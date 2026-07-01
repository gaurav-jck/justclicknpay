package com.justclick.clicknbook.Fragment.hotel

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.TrainStationModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.adapter.HotelCitySearchAdapter
import com.justclick.clicknbook.databinding.FragmentHotelSearchNewBinding
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel
import com.justclick.clicknbook.model.HotelCityListModel
import com.justclick.clicknbook.model.HotelCityListModel.CityResponse
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener
import com.justclick.clicknbook.requestmodels.HotelAvailabilityRequestModel
import com.justclick.clicknbook.requestmodels.HotelAvailabilityRequestModel.RoomOccupancy
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.EncryptionDecryptionClass
import com.justclick.clicknbook.utils.MyBounceInterpolator
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HotelSearchFragmentKotlin : Fragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener, MyHotelCitySearchDialog.OnCityDialogResult {
    val CHECK_IN: Int = 1
    val CHECK_OUT: Int = 2
    private var titleChangeListener: ToolBarTitleChangeListener? = null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null
    private var checkInDate = ""
    private var checkOutDate = ""
    private var hotelCode = ""
    private var dateServerFormat: SimpleDateFormat? = null
    private var dateMonthFormat: SimpleDateFormat? = null
    private var dayFormat: SimpleDateFormat? = null
    private var yearFormat: SimpleDateFormat? = null
    private var checkInDateDay = 0
    private var checkInDateMonth = 0
    private var checkInDateYear = 0
    private var checkOutDateDay = 0
    private var checkOutDateMonth = 0
    private var checkOutDateYear = 0
    private var checkInDateCalendar: Calendar? = null
    private var checkOutDateCalendar: Calendar? = null
    private var loginModel: LoginModel? = null
    private var NumberOfRooms = 1
    private var NumberOfAdult = 1
    private var NumberOfChild = 0
    private val NumberOfDays = 1
    private var Adults1 = 1
    private var Children1 = 0
    private val TotalRoom1 = 1
    private var Adults2 = 1
    private var Children2 = 0
    private val TotalRoom2 = 1
    private var Adults3 = 1
    private var Children3 = 0
    private val TotalRoom3 = 1
    private var Adults4 = 1
    private var Children4 = 0
    private val TotalRoom4 = 1
    private val Ages1 = ""
    private val Ages2 = ""
    private val Ages3 = ""
    private val Ages4 = ""
    private var hotelCitySearchAdapter: HotelCitySearchAdapter? = null
    private var hotelCityListModel: HotelCityListModel? = null
    private var citySelectedResponse: CityResponse? = null
    private var childAge11 = ""
    private var childAge12 = ""
    private var childAge21 = ""
    private var childAge22 = ""
    private var childAge31 = ""
    private var childAge32 = ""
    private var childAge41 = ""
    private var childAge42 = ""
    private var binding: FragmentHotelSearchNewBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            titleChangeListener = context as ToolBarTitleChangeListener
            toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
        } catch (e: ClassCastException) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hotelCityListModel = HotelCityListModel()
        citySelectedResponse = hotelCityListModel!!.CityResponse()
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHotelSearchNewBinding.inflate(inflater, container, false)
//        val view = inflater.inflate(R.layout.fragment_hotel_search_new, container, false)

        titleChangeListener!!.onToolBarTitleChange(getString(R.string.hotelSearchFragmentTitle))
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        binding!!.topView.titleTv.text="Hotel Saerch"
        //        roomInfoRel = (LinearLayout) view.findViewById(R.id.roomInfoRel);
        openRoomInfoDialog()

        initializeDates()
        setFont()

        binding!!.cardView.setOnClickListener {
            Common.hideSoftKeyboard(
                context as NavigationDrawerActivity?
            )
        }
        binding!!.topView.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        //        roomInfoRel.setOnClickListener(this);
        binding!!.returnDateLin.setOnClickListener(this)
        binding!!.departDateLin.setOnClickListener(this)
        binding!!.hotelCitySearchRel.setOnClickListener(this)
        binding!!.searchRel.setOnClickListener(this)

        return binding!!.root
    }

    private fun initializeDates() {
        //Date formats
//        dateServerFormat = Common.getShowInTVDateFormat();
        dateServerFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        dateMonthFormat = SimpleDateFormat("dd MMM", Locale.US)
        dayFormat = Common.getFullDayFormat()
        yearFormat = SimpleDateFormat("yyyy", Locale.US)

        //default checkIn Date
        checkInDateCalendar = Calendar.getInstance()
        checkInDateDay = checkInDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        checkInDateMonth = checkInDateCalendar!!.get(Calendar.MONTH)
        checkInDateYear = checkInDateCalendar!!.get(Calendar.YEAR)

        //default checkOut Date
        checkOutDateCalendar = Calendar.getInstance()
        checkOutDateCalendar!!.add(Calendar.DAY_OF_MONTH, 1)
        checkOutDateDay = checkOutDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        checkOutDateMonth = checkOutDateCalendar!!.get(Calendar.MONTH)
        checkOutDateYear = checkOutDateCalendar!!.get(Calendar.YEAR)


        checkInDate = dateServerFormat!!.format(checkInDateCalendar!!.getTime())
        checkOutDate = dateServerFormat!!.format(checkOutDateCalendar!!.getTime())

        //set default date
        binding!!.departDateTv!!.text = dateMonthFormat!!.format(checkInDateCalendar!!.getTime()).uppercase(
            Locale.getDefault()
        )
        binding!!.departDayTv!!.text =
            dayFormat!!.format(checkInDateCalendar!!.getTime()).uppercase(Locale.getDefault())
        binding!!.departYearTv!!.text =
            yearFormat!!.format(checkInDateCalendar!!.getTime()).uppercase(Locale.getDefault())
        binding!!.returnDateTv!!.text = dateMonthFormat!!.format(checkOutDateCalendar!!.getTime()).uppercase(
            Locale.getDefault()
        )
        binding!!.returnDayTv!!.text =
            dayFormat!!.format(checkOutDateCalendar!!.getTime()).uppercase(Locale.getDefault())
        binding!!.returnYearTv!!.text =
            yearFormat!!.format(checkOutDateCalendar!!.getTime()).uppercase(Locale.getDefault())
    }

    private fun setFont() {
        val face = Common.EditTextTypeFace(context)
        val face2 = Common.FlightCalenderTypeFace3(context)
        val face1 = Common.OpenSansRegularTypeFace(context)
        binding!!.departDateTv!!.typeface = face2
        binding!!.returnDateTv!!.typeface = face2
        binding!!.fromNameTv!!.typeface = face
        binding!!.searchTv!!.typeface = face1
        binding!!.fromTv.typeface = face1
        binding!!.departLabel.typeface = face1
        binding!!.roomInfoLabel.typeface = face1
        binding!!.roomLabel1.typeface = face1
        binding!!.roomLabel2.typeface = face1
        binding!!.roomLabel3.typeface = face1
        binding!!.roomLabel4.typeface = face1
    }

    private fun openCheckInDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            object : OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker,
                    year: Int,
                    monthOfYear: Int,
                    dayOfMonth: Int
                ) {
                    checkInDateCalendar!![year, monthOfYear] = dayOfMonth
                    checkInDate = dateServerFormat!!.format(checkInDateCalendar!!.time)
                    checkInDateDay = dayOfMonth
                    checkInDateMonth = monthOfYear
                    checkInDateYear = year
                    binding!!.departDateTv!!.text =
                        dateMonthFormat!!.format(checkInDateCalendar!!.time).uppercase(
                            Locale.getDefault()
                        )
                    binding!!.departDayTv!!.text = dayFormat!!.format(checkInDateCalendar!!.time)
                        .uppercase(Locale.getDefault())
                    binding!!.departYearTv!!.text = yearFormat!!.format(checkInDateCalendar!!.time).uppercase(
                        Locale.getDefault()
                    )
                    setCheckOutDate()
                    //                        calculateNights();
                    binding!!.nightTv!!.setText(days + "\nNight")
                }
            }, checkInDateYear, checkInDateMonth, checkInDateDay
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun calculateNights() {
//        long difference = today.getTimeInMillis() - calendar.getTimeInMillis();
//        int days = (int) (difference/ (1000*60*60*24));
    }

    private fun openCheckOutDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            object : OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker,
                    year: Int,
                    monthOfYear: Int,
                    dayOfMonth: Int
                ) {
                    checkOutDateCalendar!![year, monthOfYear] = dayOfMonth
                    checkOutDate = dateServerFormat!!.format(checkOutDateCalendar!!.time)
                    checkOutDateDay = dayOfMonth
                    checkOutDateMonth = monthOfYear
                    checkOutDateYear = year
                    binding!!.returnDateTv!!.text =
                        dateMonthFormat!!.format(checkOutDateCalendar!!.time).uppercase(
                            Locale.getDefault()
                        )
                    binding!!.returnDayTv!!.text = dayFormat!!.format(checkOutDateCalendar!!.time).uppercase(
                        Locale.getDefault()
                    )
                    binding!!.returnYearTv!!.text =
                        yearFormat!!.format(checkOutDateCalendar!!.time).uppercase(
                            Locale.getDefault()
                        )
                    binding!!.nightTv!!.setText(days + "\nNight")
                }
            }, checkOutDateYear, checkOutDateMonth, checkOutDateDay
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.minDate = checkInDateCalendar!!.timeInMillis - 1000
        datePickerDialog.show()
    }

    private fun setCheckOutDate() {
        if (checkInDateCalendar!!.after(checkOutDateCalendar)) {
            checkOutDateCalendar!!.time = checkInDateCalendar!!.time
            checkOutDateCalendar!!.add(Calendar.DAY_OF_MONTH, 1)
            checkOutDate = dateServerFormat!!.format(checkOutDateCalendar!!.time)
            checkOutDateDay = Calendar.DAY_OF_MONTH
            checkOutDateMonth = Calendar.MONTH
            checkOutDateYear = Calendar.YEAR
            binding!!.returnDateTv!!.text = dateMonthFormat!!.format(checkOutDateCalendar!!.time).uppercase(
                Locale.getDefault()
            )
            binding!!.returnDayTv!!.text =
                dayFormat!!.format(checkInDateCalendar!!.time).uppercase(Locale.getDefault())
            binding!!.returnYearTv!!.text =
                yearFormat!!.format(checkInDateCalendar!!.time).uppercase(Locale.getDefault())
        }
    }

    private fun getSpinnerAdapter(data: Array<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.mobile_operator_spinner_item, R.id.operator_tv, data
        )
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, "Please wait...")
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.hotelCitySearchRel -> MyHotelCitySearchDialog.showCustomDialog(
                context, "Hotel City", 1,
                this
            )

            R.id.departDateLin -> openCheckInDatePicker()
            R.id.returnDateLin -> openCheckOutDatePicker()
            R.id.roomInfoRel -> try {
//                    openRoomInfoDialog();
            } catch (e: Exception) {
            }

            R.id.searchRel -> {
                Common.preventFrequentClick(binding!!.searchRel)
                val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)

                // Use bounce interpolator with amplitude 0.2 and frequency 20
                val interpolator = MyBounceInterpolator(0.2, 20.0)
                myAnim.interpolator = interpolator

                binding!!.flightImg!!.startAnimation(myAnim)

                val bundle = Bundle()
                bundle.putSerializable("HotelList", HotelAvailabilityResponseModel().hotels)
                bundle.putSerializable("CheckInDateCalander", checkInDateCalendar)
                bundle.putSerializable("CheckOutDateCalander", checkOutDateCalendar)
                val fragment = HotelSearchListFragment()
                fragment.arguments = bundle
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                hideCustomDialog()
                if ( /*destinationCityEdt.getText().toString().length()<3 || */citySelectedResponse!!.CityName == null) {
//                    Toast.makeText(context,R.string.empty_and_invalid_city,Toast.LENGTH_LONG).show();
                } else {
                    val hotelAvailabilityRequestModel = HotelAvailabilityRequestModel()
                    hotelAvailabilityRequestModel.DoneCardUser = loginModel!!.Data.DoneCardUser
                    hotelAvailabilityRequestModel.DeviceId = Common.getDeviceId(context)
                    hotelAvailabilityRequestModel.LoginSessionId =
                        EncryptionDecryptionClass.EncryptSessionId(
                            EncryptionDecryptionClass.Decryption(
                                loginModel!!.LoginSessionId,
                                context
                            ), context
                        )
                    hotelAvailabilityRequestModel.CheckInDate = checkInDate
                    hotelAvailabilityRequestModel.CheckOutDate = checkOutDate
                    hotelAvailabilityRequestModel.CountryName = citySelectedResponse!!.Country
                    hotelAvailabilityRequestModel.DestnationName = citySelectedResponse!!.CityName
                    hotelAvailabilityRequestModel.DestnationCode = ""
                    hotelAvailabilityRequestModel.NumberOfAdult = NumberOfAdult.toString() + ""
                    hotelAvailabilityRequestModel.NumberOfChild = NumberOfChild.toString() + ""
                    hotelAvailabilityRequestModel.NumberOfDays = days
                    hotelAvailabilityRequestModel.NumberOfRooms = NumberOfRooms.toString() + ""
                    hotelAvailabilityRequestModel.Supplier = "TBO"
                    hotelAvailabilityRequestModel.StaRating = "All"
                    hotelAvailabilityRequestModel.RoomOccupancy = roomArrayList
                    //                    hotelSearch(hotelAvailabilityRequestModel);
                }
            }

            R.id.roomPlusTv -> {
                roomPlusClick()
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.roomPlusTv) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.roomMinusTv -> {
                roomMinusClick()
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.roomMinusTv) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultMinusImg1 -> {
                adultMinusClick(1)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultMinusImg1) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultPlusImg1 -> {
                adultPlusClick(1, true)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultPlusImg1) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childMinusImg1 -> {
                childImageMinusClick(1)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childMinusImg1) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childPlusImg1 -> {
                childImagePlusClick(1)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childPlusImg1) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultMinusImg2 -> {
                adultMinusClick(2)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultMinusImg2) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultPlusImg2 -> {
                adultPlusClick(2, true)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultPlusImg2) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childMinusImg2 -> {
                childImageMinusClick(2)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childMinusImg2) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childPlusImg2 -> {
                childImagePlusClick(2)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childPlusImg2) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultMinusImg3 -> {
                adultMinusClick(3)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultMinusImg3) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultPlusImg3 -> {
                adultPlusClick(3, true)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultPlusImg3) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childMinusImg3 -> {
                childImageMinusClick(3)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childMinusImg3) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childPlusImg3 -> {
                childImagePlusClick(3)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childPlusImg3) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultMinusImg4 -> {
                adultMinusClick(4)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultMinusImg4) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.adultPlusImg4 -> {
                adultPlusClick(4, true)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.adultPlusImg4) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childMinusImg4 -> {
                childImageMinusClick(4)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childMinusImg4) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }

            R.id.childPlusImg4 -> {
                childImagePlusClick(4)
                ObjectAnimator.ofObject(
                    v.findViewById<View>(R.id.childPlusImg4) as ImageView,
                    "backgroundColor",
                    ArgbEvaluator(),  /*Red*/
                    -0x772bf5f6,  /*Blue*/
                    0x00ffffff
                ).setDuration(500).start()
            }
        }
    }

    val days: String
        get() = ((checkOutDateCalendar!!.timeInMillis - checkInDateCalendar!!.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
            .toString()

    private fun adultPlusClick(room: Int, isToast: Boolean) {
        when (room) {
            1 -> {
                if (Adults1 == 1) {
                    binding!!.adultImg11!!.visibility = View.VISIBLE
                    binding!!.adultImg12!!.visibility = View.VISIBLE
                    Adults1++
                } else if (Adults1 == 2) {
                    binding!!.adultImg11!!.visibility = View.VISIBLE
                    binding!!.adultImg12!!.visibility = View.VISIBLE
                    binding!!.adultImg13!!.visibility = View.VISIBLE
                    Adults1++
                } else if (Adults1 == 3) {
                    binding!!.adultImg11!!.visibility = View.VISIBLE
                    binding!!.adultImg12!!.visibility = View.VISIBLE
                    binding!!.adultImg13!!.visibility = View.VISIBLE
                    binding!!.adultImg14!!.visibility = View.VISIBLE
                    Adults1++
                } else {
                    binding!!.adultImg11!!.visibility = View.VISIBLE
                    binding!!.adultImg12!!.visibility = View.VISIBLE
                    binding!!.adultImg13!!.visibility = View.VISIBLE
                    binding!!.adultImg14!!.visibility = View.VISIBLE
                    if (isToast) {
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                binding!!.roomAdultCount1!!.text = Adults1.toString() + ""
            }

            2 -> {
                if (Adults2 == 1) {
                    binding!!.adultImg21!!.visibility = View.VISIBLE
                    binding!!.adultImg22!!.visibility = View.VISIBLE
                    Adults2++
                } else if (Adults2 == 2) {
                    binding!!.adultImg21!!.visibility = View.VISIBLE
                    binding!!.adultImg22!!.visibility = View.VISIBLE
                    binding!!.adultImg23!!.visibility = View.VISIBLE
                    Adults2++
                } else if (Adults2 == 3) {
                    binding!!.adultImg21!!.visibility = View.VISIBLE
                    binding!!.adultImg22!!.visibility = View.VISIBLE
                    binding!!.adultImg23!!.visibility = View.VISIBLE
                    binding!!.adultImg24!!.visibility = View.VISIBLE
                    Adults2++
                } else if (Adults2 == 4) {
                    binding!!.adultImg21!!.visibility = View.VISIBLE
                    binding!!.adultImg22!!.visibility = View.VISIBLE
                    binding!!.adultImg23!!.visibility = View.VISIBLE
                    binding!!.adultImg24!!.visibility = View.VISIBLE
                    if (isToast) {
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    binding!!.adultImg21!!.visibility = View.VISIBLE
                    binding!!.adultImg22!!.visibility = View.VISIBLE
                    binding!!.adultImg23!!.visibility = View.VISIBLE
                    binding!!.adultImg24!!.visibility = View.VISIBLE
                }
                binding!!.roomAdultCount2!!.text = Adults2.toString() + ""
            }

            3 -> {
                if (Adults3 == 1) {
                    binding!!.adultImg31!!.visibility = View.VISIBLE
                    binding!!.adultImg32!!.visibility = View.VISIBLE
                    Adults3++
                } else if (Adults3 == 2) {
                    binding!!.adultImg31!!.visibility = View.VISIBLE
                    binding!!.adultImg32!!.visibility = View.VISIBLE
                    binding!!.adultImg33!!.visibility = View.VISIBLE
                    Adults3++
                } else if (Adults3 == 3) {
                    binding!!.adultImg31!!.visibility = View.VISIBLE
                    binding!!.adultImg32!!.visibility = View.VISIBLE
                    binding!!.adultImg33!!.visibility = View.VISIBLE
                    binding!!.adultImg34!!.visibility = View.VISIBLE
                    Adults3++
                } else if (Adults3 == 4) {
                    binding!!.adultImg31!!.visibility = View.VISIBLE
                    binding!!.adultImg32!!.visibility = View.VISIBLE
                    binding!!.adultImg33!!.visibility = View.VISIBLE
                    binding!!.adultImg34!!.visibility = View.VISIBLE
                    if (isToast) {
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    binding!!.adultImg31!!.visibility = View.VISIBLE
                    binding!!.adultImg32!!.visibility = View.VISIBLE
                    binding!!.adultImg33!!.visibility = View.VISIBLE
                    binding!!.adultImg34!!.visibility = View.VISIBLE
                }
                binding!!.roomAdultCount3!!.text = Adults3.toString() + ""
            }

            4 -> {
                if (Adults4 == 1) {
                    binding!!.adultImg41!!.visibility = View.VISIBLE
                    binding!!.adultImg42!!.visibility = View.VISIBLE
                    Adults4++
                } else if (Adults4 == 2) {
                    binding!!.adultImg41!!.visibility = View.VISIBLE
                    binding!!.adultImg42!!.visibility = View.VISIBLE
                    binding!!.adultImg43!!.visibility = View.VISIBLE
                    Adults4++
                } else if (Adults4 == 3) {
                    binding!!.adultImg41!!.visibility = View.VISIBLE
                    binding!!.adultImg42!!.visibility = View.VISIBLE
                    binding!!.adultImg43!!.visibility = View.VISIBLE
                    binding!!.adultImg44!!.visibility = View.VISIBLE
                    Adults4++
                } else if (Adults4 == 4) {
                    binding!!.adultImg41!!.visibility = View.VISIBLE
                    binding!!.adultImg42!!.visibility = View.VISIBLE
                    binding!!.adultImg43!!.visibility = View.VISIBLE
                    binding!!.adultImg44!!.visibility = View.VISIBLE
                    if (isToast) {
                        Toast.makeText(context, R.string.hotel_max_adults, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    binding!!.adultImg41!!.visibility = View.VISIBLE
                    binding!!.adultImg42!!.visibility = View.VISIBLE
                    binding!!.adultImg43!!.visibility = View.VISIBLE
                    binding!!.adultImg44!!.visibility = View.VISIBLE
                }
                binding!!.roomAdultCount4!!.text = Adults4.toString() + ""
            }
        }
    }

    private fun adultMinusClick(room: Int) {
        when (room) {
            1 -> {
                if (Adults1 == 2) {
                    binding!!.adultImg12!!.visibility = View.GONE
                    binding!!.adultImg13!!.visibility = View.GONE
                    binding!!.adultImg14!!.visibility = View.GONE
                    Adults1--
                } else if (Adults1 == 3) {
                    binding!!.adultImg13!!.visibility = View.GONE
                    binding!!.adultImg14!!.visibility = View.GONE
                    Adults1--
                } else if (Adults1 == 4) {
                    binding!!.adultImg14!!.visibility = View.GONE
                    Adults1--
                } else {
                    binding!!.roomAdultCount1!!.text = Adults1.toString() + ""
                }
                binding!!.roomAdultCount1!!.text = Adults1.toString() + ""
            }

            2 -> {
                if (Adults2 == 2) {
                    binding!!.adultImg22!!.visibility = View.GONE
                    binding!!.adultImg23!!.visibility = View.GONE
                    binding!!.adultImg24!!.visibility = View.GONE
                    Adults2--
                } else if (Adults2 == 3) {
                    binding!!.adultImg23!!.visibility = View.GONE
                    binding!!.adultImg24!!.visibility = View.GONE
                    Adults2--
                } else if (Adults2 == 4) {
                    binding!!.adultImg24!!.visibility = View.GONE
                    Adults2--
                } else {
                    binding!!.roomAdultCount2!!.text = Adults2.toString() + ""
                }
                binding!!.roomAdultCount2!!.text = Adults2.toString() + ""
            }

            3 -> {
                if (Adults3 == 2) {
                    binding!!.adultImg32!!.visibility = View.GONE
                    binding!!.adultImg33!!.visibility = View.GONE
                    binding!!.adultImg34!!.visibility = View.GONE
                    Adults3--
                } else if (Adults3 == 3) {
                    binding!!.adultImg33!!.visibility = View.GONE
                    binding!!.adultImg34!!.visibility = View.GONE
                    Adults3--
                } else if (Adults3 == 4) {
                    binding!!.adultImg34!!.visibility = View.GONE
                    Adults3--
                } else {
                    binding!!.roomAdultCount3!!.text = Adults3.toString() + ""
                }
                binding!!.roomAdultCount3!!.text = Adults3.toString() + ""
            }

            4 -> {
                if (Adults4 == 2) {
                    binding!!.adultImg42!!.visibility = View.GONE
                    binding!!.adultImg43!!.visibility = View.GONE
                    binding!!.adultImg44!!.visibility = View.GONE
                    Adults4--
                } else if (Adults4 == 3) {
                    binding!!.adultImg43!!.visibility = View.GONE
                    binding!!.adultImg44!!.visibility = View.GONE
                    Adults4--
                } else if (Adults4 == 4) {
                    binding!!.adultImg44!!.visibility = View.GONE
                    Adults4--
                } else {
                    binding!!.roomAdultCount4!!.text = Adults4.toString() + ""
                }
                binding!!.roomAdultCount4!!.text = Adults4.toString() + ""
            }
        }
    }

    private fun roomMinusClick() {
        if (NumberOfRooms == 2) {
            binding!!.roomTwoLin!!.visibility = View.GONE
            NumberOfRooms--
        } else if (NumberOfRooms == 3) {
            binding!!.roomThreeLin!!.visibility = View.GONE
            NumberOfRooms--
        } else if (NumberOfRooms == 4) {
            binding!!.roomFourLin!!.visibility = View.GONE
            NumberOfRooms--
        }
        binding!!.roomCountTv!!.text = NumberOfRooms.toString() + ""
    }

    private fun roomPlusClick() {
        if (NumberOfRooms == 1) {
            binding!!.roomTwoLin!!.visibility = View.VISIBLE
            NumberOfRooms++
        } else if (NumberOfRooms == 2) {
            binding!!.roomThreeLin!!.visibility = View.VISIBLE
            NumberOfRooms++
        } else if (NumberOfRooms == 3) {
            binding!!.roomFourLin!!.visibility = View.VISIBLE
            NumberOfRooms++
        } else {
            Toast.makeText(context, R.string.hotel_max_room, Toast.LENGTH_SHORT).show()
        }
        binding!!.roomCountTv!!.text = NumberOfRooms.toString() + ""
    }

    private fun childImagePlusClick(i: Int) {
        when (i) {
            1 -> {
                if (Children1 == 0) {
                    binding!!.childImg11!!.visibility = View.GONE
                    binding!!.childImg12!!.visibility = View.VISIBLE
                    binding!!.childAgeLin11!!.visibility = View.VISIBLE
                    Children1++
                } else if (Children1 == 1) {
                    binding!!.childImg13!!.visibility = View.VISIBLE
                    binding!!.childAgeLin12!!.visibility = View.VISIBLE
                    Children1++
                } else if (Children1 == 2) {
                    binding!!.childImg14!!.visibility = View.VISIBLE
                    binding!!.childAgeLin13!!.visibility = View.VISIBLE
                    Children1++
                } /*else if (Children1 == 3) {
                    binding!!.childImg14!!.visibility = View.VISIBLE
                    binding!!.childAgeLin14!!.visibility = View.VISIBLE
                    Children1++
                }*/
                binding!!.roomChildCount1!!.text = Children1.toString() + ""
            }

            2 -> {
                if (Children2 == 0) {
                    binding!!.childImg21!!.visibility = View.GONE
                    binding!!.childImg22!!.visibility = View.VISIBLE
                    binding!!.childAgeLin21!!.visibility = View.VISIBLE
                    Children2++
                } else if (Children2 == 1) {
                    binding!!.childImg23!!.visibility = View.VISIBLE
                    binding!!.childAgeLin22!!.visibility = View.VISIBLE
                    Children2++
                } else if (Children2 == 2) {
                    binding!!.childImg24!!.visibility = View.VISIBLE
                    binding!!.childAgeLin23!!.visibility = View.VISIBLE
                    Children2++
                } /*else if (Children2 == 3) {
                    binding!!.childImg24!!.visibility = View.VISIBLE
                    binding!!.childAgeLin24!!.visibility = View.VISIBLE
                    Children2++
                }*/
                binding!!.roomChildCount2!!.text = Children2.toString() + ""
            }

            3 -> {
                if (Children3 == 0) {
                    binding!!.childImg31!!.visibility = View.GONE
                    binding!!.childImg32!!.visibility = View.VISIBLE
                    binding!!.childAgeLin31!!.visibility = View.VISIBLE
                    Children3++
                } else if (Children3 == 1) {
                    binding!!.childImg33!!.visibility = View.VISIBLE
                    binding!!.childAgeLin32!!.visibility = View.VISIBLE
                    Children3++
                } else if (Children3 == 2) {
                    binding!!.childImg34!!.visibility = View.VISIBLE
                    binding!!.childAgeLin33!!.visibility = View.VISIBLE
                    Children3++
                }/* else if (Children3 == 3) {
                    binding!!.childImg34!!.visibility = View.VISIBLE
                    binding!!.childAgeLin34!!.visibility = View.VISIBLE
                    Children3++
                }*/
                binding!!.roomChildCount3!!.text = Children3.toString() + ""
            }

            4 -> {
                if (Children4 == 0) {
                    binding!!.childImg41!!.visibility = View.VISIBLE
                    binding!!.childAgeLin41!!.visibility = View.VISIBLE
                    binding!!.childAgeLin42!!.visibility = View.GONE
                    Children4++
                } else if (Children4 == 1) {
                    binding!!.childImg42!!.visibility = View.VISIBLE
                    binding!!.childAgeLin42!!.visibility = View.VISIBLE
                    Children4++
                } else if (Children4 == 2) {
                    binding!!.childImg43!!.visibility = View.VISIBLE
                    binding!!.childAgeLin43!!.visibility = View.VISIBLE
                    Children4++
                }/* else if (Children4 == 3) {
                    binding!!.childImg44!!.visibility = View.VISIBLE
                    binding!!.childAgeLin44!!.visibility = View.VISIBLE
                    Children4++
                }*/
                binding!!.roomChildCount4!!.text = Children4.toString() + ""
            }
        }
    }

    private fun childImageMinusClick(i: Int) {
        when (i) {
            1 -> {
                if (Children1 == 1) {
                    binding!!.childImg11!!.visibility = View.VISIBLE
                    binding!!.childImg12!!.visibility = View.GONE
                    binding!!.childAgeLin11!!.visibility = View.GONE
                    Children1--
                } else if (Children1 == 2) {
                    binding!!.childImg13!!.visibility = View.GONE
                    binding!!.childAgeLin12!!.visibility = View.GONE
                    Children1--
                } else if (Children1 == 3) {
                    binding!!.childImg14!!.visibility = View.GONE
                    binding!!.childAgeLin13!!.visibility = View.GONE
                    Children1--
                } else if (Children1 == 4) {
                    binding!!.childImg15!!.visibility = View.GONE
                    binding!!.childAgeLin14!!.visibility = View.GONE
                    Children1--
                } else {
                    binding!!.childImg11!!.visibility = View.VISIBLE
                    binding!!.childImg12!!.visibility = View.GONE
                    binding!!.childAgeLin11!!.visibility = View.GONE
                }
                binding!!.roomChildCount1!!.text = Children1.toString() + ""
            }

            2 -> {
                if (Children2 == 1) {
                    binding!!.childImg21!!.visibility = View.VISIBLE
                    binding!!.childImg22!!.visibility = View.GONE
                    binding!!.childAgeLin21!!.visibility = View.GONE
                    Children2--
                } else if (Children2 == 2) {
                    binding!!.childImg23!!.visibility = View.GONE
                    binding!!.childAgeLin22!!.visibility = View.GONE
                    Children2--
                } else if (Children2 == 3) {
                    binding!!.childImg24!!.visibility = View.GONE
                    binding!!.childAgeLin23!!.visibility = View.GONE
                    Children2--
                } else if (Children2 == 4) {
                    binding!!.childImg25!!.visibility = View.GONE
                    binding!!.childAgeLin24!!.visibility = View.GONE
                    Children2--
                } else {
                    binding!!.childImg21!!.visibility = View.VISIBLE
                    binding!!.childImg22!!.visibility = View.GONE
                    binding!!.childAgeLin21!!.visibility = View.GONE
                }
                binding!!.roomChildCount2!!.text = Children2.toString() + ""
            }

            3 -> {
                if (Children3 == 1) {
                    binding!!.childImg31!!.visibility = View.VISIBLE
                    binding!!.childImg32!!.visibility = View.GONE
                    binding!!.childAgeLin31!!.visibility = View.GONE
                    Children3--
                } else if (Children3 == 2) {
                    binding!!.childImg33!!.visibility = View.GONE
                    binding!!.childAgeLin32!!.visibility = View.GONE
                    Children3--
                } else if (Children3 == 3) {
                    binding!!.childImg34!!.visibility = View.GONE
                    binding!!.childAgeLin33!!.visibility = View.GONE
                    Children3--
                } else if (Children3 == 4) {
                    binding!!.childImg35!!.visibility = View.GONE
                    binding!!.childAgeLin34!!.visibility = View.GONE
                    Children3--
                } else {
                    binding!!.childImg31!!.visibility = View.VISIBLE
                    binding!!.childImg32!!.visibility = View.GONE
                    binding!!.childAgeLin31!!.visibility = View.GONE
                }
                binding!!.roomChildCount3!!.text = Children3.toString() + ""
            }

            4 -> {
                if (Children4 == 1) {
                    binding!!.childImg41!!.visibility = View.VISIBLE
                    binding!!.childImg42!!.visibility = View.GONE
                    binding!!.childAgeLin41!!.visibility = View.GONE
                    Children4--
                } else if (Children4 == 2) {
                    binding!!.childImg43!!.visibility = View.GONE
                    binding!!.childAgeLin42!!.visibility = View.GONE
                    Children4--
                } else if (Children4 == 3) {
                    binding!!.childImg44!!.visibility = View.GONE
                    binding!!.childAgeLin43!!.visibility = View.GONE
                    Children4--
                } else if (Children4 == 4) {
                    binding!!.childImg45!!.visibility = View.GONE
                    binding!!.childAgeLin44!!.visibility = View.GONE
                    Children4--
                } else {
                    binding!!.childImg41!!.visibility = View.GONE
                    binding!!.childImg42!!.visibility = View.GONE
                    binding!!.childAgeLin41!!.visibility = View.GONE
                }
                binding!!.roomChildCount4!!.text = Children4.toString() + ""
            }
        }
    }

    private fun openRoomInfoDialog() {

        binding!!.childAgeSpinner14!!.onItemSelectedListener = this
        binding!!.childAgeSpinner13!!.onItemSelectedListener = this
        binding!!.childAgeSpinner12!!.onItemSelectedListener = this
        binding!!.childAgeSpinner11!!.onItemSelectedListener = this


        binding!!.childAgeSpinner21!!.onItemSelectedListener = this
        binding!!.childAgeSpinner22!!.onItemSelectedListener = this
        binding!!.childAgeSpinner23!!.onItemSelectedListener = this
        binding!!.childAgeSpinner24!!.onItemSelectedListener = this

        binding!!.childAgeSpinner31!!.onItemSelectedListener = this
        binding!!.childAgeSpinner32!!.onItemSelectedListener = this
        binding!!.childAgeSpinner33!!.onItemSelectedListener = this
        binding!!.childAgeSpinner34!!.onItemSelectedListener = this


        binding!!.childAgeSpinner41!!.onItemSelectedListener = this
        binding!!.childAgeSpinner42!!.onItemSelectedListener = this
        binding!!.childAgeSpinner43!!.onItemSelectedListener = this
        binding!!.childAgeSpinner44!!.onItemSelectedListener = this

        binding!!.roomPlusTv.setOnClickListener(this)
        binding!!.roomMinusTv.setOnClickListener(this)

        binding!!.adultMinusImg1.setOnClickListener(this)
        binding!!.adultPlusImg1.setOnClickListener(this)
        binding!!.childPlusImg1.setOnClickListener(this)
        binding!!.childMinusImg1.setOnClickListener(this)

        binding!!.adultMinusImg2.setOnClickListener(this)
        binding!!.adultPlusImg2.setOnClickListener(this)
        binding!!.childPlusImg2.setOnClickListener(this)
        binding!!.childMinusImg2.setOnClickListener(this)

        binding!!.adultMinusImg3.setOnClickListener(this)
        binding!!.adultPlusImg3.setOnClickListener(this)
        binding!!.childPlusImg3.setOnClickListener(this)
        binding!!.childMinusImg3.setOnClickListener(this)

        binding!!.adultMinusImg4.setOnClickListener(this)
        binding!!.adultPlusImg4.setOnClickListener(this)
        binding!!.childPlusImg4.setOnClickListener(this)
        binding!!.childMinusImg4.setOnClickListener(this)

        roomDefaultValues()
    }

    private fun setValues() {
        if (NumberOfRooms == 1) {
//            paxInfoTv.setText(Adults1+" Adult, "+Children1+ " Child");
            NumberOfAdult = Adults1
            NumberOfChild = Children1
        } else if (NumberOfRooms == 2) {
//            paxInfoTv.setText((Adults1+Adults2)+" Adult, "+(Children1+Children2)+ " Child");
            NumberOfAdult = Adults1 + Adults2
            NumberOfChild = Children1 + Children2
        } else if (NumberOfRooms == 3) {
//            paxInfoTv.setText((Adults1+Adults2+Adults3)+" Adult, "+(Children1+Children2+Children3)+ " Child");
            NumberOfAdult = Adults1 + Adults2 + Adults3
            NumberOfChild = Children1 + Children2 + Children3
        } else {
//            paxInfoTv.setText((Adults1+Adults2+Adults3+Adults4)+" Adult, "+(Children1+Children2+Children3+Children4)+ " Child");
            NumberOfAdult = Adults1 + Adults2 + Adults3 + Adults4
            NumberOfChild = Children1 + Children2 + Children3 + Children4
        }
        if (NumberOfRooms == 1) {
//            totalRoomsTv.setText(NumberOfRooms + " Room");
        } else {
//            totalRoomsTv.setText(NumberOfRooms + " Rooms");
        }
    }

    private fun roomDefaultValues() {
        childAge11 = "1"
        childAge12 = "1"
        childAge21 = "1"
        childAge22 = "1"
        childAge31 = "1"
        childAge32 = "1"
        childAge41 = "1"
        childAge42 = "1"
        binding!!.roomCountTv!!.text = NumberOfRooms.toString() + ""
        adultDefaultValue(1)
        childDefaultValue(1)

        if (NumberOfRooms == 2) {
            adultDefaultValue(2)
            childDefaultValue(2)
            binding!!.roomTwoLin!!.visibility = View.VISIBLE
        } else if (NumberOfRooms == 3) {
            adultDefaultValue(2)
            adultDefaultValue(3)
            childDefaultValue(2)
            childDefaultValue(3)
            binding!!.roomTwoLin!!.visibility = View.VISIBLE
            binding!!.roomThreeLin!!.visibility = View.VISIBLE
        } else if (NumberOfRooms == 4) {
            adultDefaultValue(2)
            adultDefaultValue(3)
            adultDefaultValue(4)
            childDefaultValue(2)
            childDefaultValue(3)
            childDefaultValue(4)
            binding!!.roomTwoLin!!.visibility = View.VISIBLE
            binding!!.roomThreeLin!!.visibility = View.VISIBLE
            binding!!.roomFourLin!!.visibility = View.VISIBLE
        }
    }

    private fun childDefaultValue(room: Int) {
        when (room) {
            1 -> if (Children1 == 0) {
                binding!!.childImg11!!.visibility = View.VISIBLE
                binding!!.childImg12!!.visibility = View.GONE
                binding!!.childAgeLin11!!.visibility = View.GONE
            } else if (Children1 == 1) {
                binding!!.childImg11!!.visibility = View.GONE
                binding!!.childImg12!!.visibility = View.VISIBLE
                binding!!.childAgeLin11!!.visibility = View.VISIBLE
                binding!!.childAgeLin12!!.visibility = View.GONE
            } else {
                binding!!.childImg11!!.visibility = View.GONE
                binding!!.childImg12!!.visibility = View.VISIBLE
                binding!!.childAgeLin11!!.visibility = View.VISIBLE
                binding!!.childAgeLin12!!.visibility = View.VISIBLE
            }

            2 -> if (Children2 == 0) {
                binding!!.childImg21!!.visibility = View.VISIBLE
                binding!!.childImg22!!.visibility = View.GONE
                binding!!.childAgeLin21!!.visibility = View.GONE
            } else if (Children2 == 1) {
                binding!!.childImg21!!.visibility = View.GONE
                binding!!.childImg22!!.visibility = View.GONE
                binding!!.childAgeLin21!!.visibility = View.VISIBLE
                binding!!.childAgeLin22!!.visibility = View.GONE
            } else {
                binding!!.childImg21!!.visibility = View.GONE
                binding!!.childImg22!!.visibility = View.VISIBLE
                binding!!.childAgeLin21!!.visibility = View.VISIBLE
                binding!!.childAgeLin22!!.visibility = View.VISIBLE
            }

            3 -> if (Children3 == 0) {
                binding!!.childImg31!!.visibility = View.VISIBLE
                binding!!.childImg32!!.visibility = View.GONE
                binding!!.childAgeLin31!!.visibility = View.GONE
            } else if (Children3 == 1) {
                binding!!.childImg31!!.visibility = View.GONE
                binding!!.childImg32!!.visibility = View.VISIBLE
                binding!!.childAgeLin31!!.visibility = View.VISIBLE
                binding!!.childAgeLin32!!.visibility = View.GONE
            } else {
                binding!!.childImg31!!.visibility = View.VISIBLE
                binding!!.childImg32!!.visibility = View.VISIBLE
                binding!!.childAgeLin31!!.visibility = View.VISIBLE
                binding!!.childAgeLin32!!.visibility = View.VISIBLE
            }

            4 -> if (Children4 == 0) {
                binding!!.childImg41!!.visibility = View.GONE
                binding!!.childImg42!!.visibility = View.GONE
                binding!!.childAgeLin41!!.visibility = View.GONE
            } else if (Children4 == 1) {
                binding!!.childImg41!!.visibility = View.VISIBLE
                binding!!.childImg42!!.visibility = View.GONE
                binding!!.childAgeLin41!!.visibility = View.VISIBLE
                binding!!.childAgeLin42!!.visibility = View.GONE
            } else {
                binding!!.childImg41!!.visibility = View.VISIBLE
                binding!!.childImg42!!.visibility = View.VISIBLE
                binding!!.childAgeLin41!!.visibility = View.VISIBLE
                binding!!.childAgeLin42!!.visibility = View.VISIBLE
            }
        }
    }

    private fun adultDefaultValue(room: Int) {
        when (room) {
            1 -> if (Adults1 == 1) {
                binding!!.adultImg11!!.visibility = View.VISIBLE
                binding!!.adultImg12!!.visibility = View.GONE
                binding!!.adultImg13!!.visibility = View.GONE
                binding!!.adultImg14!!.visibility = View.GONE
            } else if (Adults1 == 2) {
                binding!!.adultImg11!!.visibility = View.VISIBLE
                binding!!.adultImg12!!.visibility = View.VISIBLE
                binding!!.adultImg13!!.visibility = View.GONE
                binding!!.adultImg14!!.visibility = View.GONE
            } else if (Adults1 == 3) {
                binding!!.adultImg11!!.visibility = View.VISIBLE
                binding!!.adultImg12!!.visibility = View.VISIBLE
                binding!!.adultImg13!!.visibility = View.VISIBLE
                binding!!.adultImg14!!.visibility = View.GONE
            } else {
                binding!!.adultImg11!!.visibility = View.VISIBLE
                binding!!.adultImg12!!.visibility = View.VISIBLE
                binding!!.adultImg13!!.visibility = View.VISIBLE
                binding!!.adultImg14!!.visibility = View.VISIBLE
            }

            2 -> if (Adults2 == 1) {
                binding!!.adultImg21!!.visibility = View.VISIBLE
                binding!!.adultImg22!!.visibility = View.GONE
                binding!!.adultImg23!!.visibility = View.GONE
                binding!!.adultImg24!!.visibility = View.GONE
            } else if (Adults2 == 2) {
                binding!!.adultImg21!!.visibility = View.VISIBLE
                binding!!.adultImg22!!.visibility = View.VISIBLE
                binding!!.adultImg23!!.visibility = View.GONE
                binding!!.adultImg24!!.visibility = View.GONE
            } else if (Adults2 == 3) {
                binding!!.adultImg21!!.visibility = View.VISIBLE
                binding!!.adultImg22!!.visibility = View.VISIBLE
                binding!!.adultImg23!!.visibility = View.VISIBLE
                binding!!.adultImg24!!.visibility = View.GONE
            } else {
                binding!!.adultImg21!!.visibility = View.VISIBLE
                binding!!.adultImg22!!.visibility = View.VISIBLE
                binding!!.adultImg23!!.visibility = View.VISIBLE
                binding!!.adultImg24!!.visibility = View.VISIBLE
            }

            3 -> if (Adults3 == 1) {
                binding!!.adultImg31!!.visibility = View.VISIBLE
                binding!!.adultImg32!!.visibility = View.GONE
                binding!!.adultImg33!!.visibility = View.GONE
                binding!!.adultImg34!!.visibility = View.GONE
            } else if (Adults3 == 2) {
                binding!!.adultImg31!!.visibility = View.VISIBLE
                binding!!.adultImg32!!.visibility = View.VISIBLE
                binding!!.adultImg33!!.visibility = View.GONE
                binding!!.adultImg34!!.visibility = View.GONE
            } else if (Adults3 == 3) {
                binding!!.adultImg31!!.visibility = View.VISIBLE
                binding!!.adultImg32!!.visibility = View.VISIBLE
                binding!!.adultImg33!!.visibility = View.VISIBLE
                binding!!.adultImg34!!.visibility = View.GONE
            } else {
                binding!!.adultImg31!!.visibility = View.VISIBLE
                binding!!.adultImg32!!.visibility = View.VISIBLE
                binding!!.adultImg33!!.visibility = View.VISIBLE
                binding!!.adultImg34!!.visibility = View.VISIBLE
            }

            4 -> if (Adults4 == 1) {
                binding!!.adultImg41!!.visibility = View.VISIBLE
                binding!!.adultImg42!!.visibility = View.GONE
                binding!!.adultImg43!!.visibility = View.GONE
                binding!!.adultImg44!!.visibility = View.GONE
            } else if (Adults4 == 2) {
                binding!!.adultImg41!!.visibility = View.VISIBLE
                binding!!.adultImg42!!.visibility = View.VISIBLE
                binding!!.adultImg43!!.visibility = View.GONE
                binding!!.adultImg44!!.visibility = View.GONE
            } else if (Adults4 == 3) {
                binding!!.adultImg41!!.visibility = View.VISIBLE
                binding!!.adultImg42!!.visibility = View.VISIBLE
                binding!!.adultImg43!!.visibility = View.VISIBLE
                binding!!.adultImg44!!.visibility = View.GONE
            } else {
                binding!!.adultImg41!!.visibility = View.VISIBLE
                binding!!.adultImg42!!.visibility = View.VISIBLE
                binding!!.adultImg43!!.visibility = View.VISIBLE
                binding!!.adultImg44!!.visibility = View.VISIBLE
            }
        }
    }

    fun hotelSearch(busRequestModel: HotelAvailabilityRequestModel?) {
        showCustomDialog()
        val apiService =
            APIClient.getClientHotelAvail().create(ApiInterface::class.java)
        val call = apiService.getHotelAvailPost(ApiConstants.HotelAvail, busRequestModel)
        call.enqueue(object : Callback<HotelAvailabilityResponseModel?> {
            override fun onResponse(
                call: Call<HotelAvailabilityResponseModel?>,
                response: Response<HotelAvailabilityResponseModel?>
            ) {
                try {
//                    arrayList.clear();
                    if (response?.body() != null) {
                        if (response.body()!!.StatusCode.equals(
                                "0",
                                ignoreCase = true
                            ) && response.body()!!.hotels.size > 0
                        ) {
                            val bundle = Bundle()
                            bundle.putSerializable("HotelList", response.body()!!.hotels)
                            bundle.putSerializable("CheckInDateCalander", checkInDateCalendar)
                            bundle.putSerializable("CheckOutDateCalander", checkOutDateCalendar)
                            val fragment = HotelSearchListFragment()
                            fragment.arguments = bundle
                            (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                                fragment
                            )
                            hideCustomDialog()
                            //                            Toast.makeText(context,response.body().Status, Toast.LENGTH_LONG).show();
                        } else if (response.body()!!.StatusCode.equals("2", ignoreCase = true)) {
                            hideCustomDialog()
                            //                            creditReportDataArrayList.addAll(response.body().Data);
//                            creditReportAdapter.notifyDataSetChanged();
                            Toast.makeText(context, response.body()!!.Status, Toast.LENGTH_LONG)
                                .show()
                        } else {
                            hideCustomDialog()
                            //                            busSearchArrayList.clear();
//                            busSearchListAdapter.notifyDataSetChanged();
                            Toast.makeText(context, response.body()!!.Status, Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        hideCustomDialog()
                        //                        busSearchArrayList.clear();
//                        busSearchListAdapter.notifyDataSetChanged();
                        Toast.makeText(
                            context,
                            R.string.response_failure_message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<HotelAvailabilityResponseModel?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.childAgeSpinner11 -> childAge11 =
                binding!!.childAgeSpinner11!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            R.id.childAgeSpinner12 -> childAge12 =
                binding!!.childAgeSpinner12!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            R.id.childAgeSpinner21 -> childAge21 =
                binding!!.childAgeSpinner21!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            R.id.childAgeSpinner22 -> childAge22 =
                binding!!.childAgeSpinner22!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            R.id.childAgeSpinner31 -> childAge31 =
                binding!!.childAgeSpinner31!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            R.id.childAgeSpinner32 -> childAge32 =
                binding!!.childAgeSpinner32!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            R.id.childAgeSpinner41 -> childAge41 =
                binding!!.childAgeSpinner41!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            R.id.childAgeSpinner42 -> childAge42 =
                binding!!.childAgeSpinner42!!.selectedItem.toString().split("\\s".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    val roomArrayList: ArrayList<RoomOccupancy>
        get() {
            val roomOccupancyArrayList = ArrayList<RoomOccupancy>()
            for (i in 1..NumberOfRooms) {
                val roomOccupancy = HotelAvailabilityRequestModel().RoomOccupancy()
                if (i == 1) {
                    roomOccupancy.Adults = Adults1
                    roomOccupancy.Children = Children1
                    if (roomOccupancy.Children == 1) {
                        roomOccupancy.Ages = childAge11
                    } else {
                        roomOccupancy.Ages = "$childAge11,$childAge12"
                    }

                    //                        roomOccupancyArrayList.add(roomOccupancy);
                } else if (i == 2) {
                    //                        roomOccupancy.Ages = childAge12;
                    roomOccupancy.Adults = Adults2
                    roomOccupancy.Children = Children2
                    if (roomOccupancy.Children == 1) {
                        roomOccupancy.Ages = childAge21
                    } else {
                        roomOccupancy.Ages = "$childAge21,$childAge22"
                    }

                    //                        roomOccupancyArrayList.add(roomOccupancy);
                } else if (i == 3) {
                    //                        roomOccupancy.Ages = Ages3;
                    roomOccupancy.Adults = Adults3
                    roomOccupancy.Children = Children3
                    if (roomOccupancy.Children == 1) {
                        roomOccupancy.Ages = childAge31
                    } else {
                        roomOccupancy.Ages = "$childAge31,$childAge32"
                    }

                    //                        roomOccupancyArrayList.add(roomOccupancy);
                } else if (i == 4) {
                    //                        roomOccupancy.Ages = Ages4;
                    roomOccupancy.Adults = Adults4
                    roomOccupancy.Children = Children4
                    if (roomOccupancy.Children == 1) {
                        roomOccupancy.Ages = childAge41
                    } else {
                        roomOccupancy.Ages = "$childAge41,$childAge42"
                    }

                    //                        roomOccupancyArrayList.add(roomOccupancy);
                }
                roomOccupancyArrayList.add(roomOccupancy)
            }
            return roomOccupancyArrayList
        }

    override fun setResult(value: Int, intent: TrainStationModel.Items) {
        binding!!.fromNameTv!!.text = intent.city_name
        hotelCode = intent.station_code!!
        MyPreferences.setFlightFromCity(context, intent.city_name)
    }
}