package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.justclick.clicknbook.Fragment.train.model.PnrStatusResponse
import com.justclick.clicknbook.R
import kotlinx.android.synthetic.main.fragment_train_pnr_status_detail.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TrainChangeBoardingStnFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var pnrResponse: PnrStatusResponse? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pnrResponse = it.getSerializable(ARG_PARAM1) as PnrStatusResponse
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_train_change_boarding, container, false)

        if(pnrResponse!=null){
//            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            setPnrStatusData(view,pnrResponse!!)
        }

        view.back_arrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun setPnrStatusData(view: View, pnrResponse: PnrStatusResponse) {
        view.pnrTv.setText("PNR : "+pnrResponse.pnrNumber)
        view.trainNameTv.setText(pnrResponse.trainName+" ("+pnrResponse.trainNumber+")")
        view.fromStnTv.setText(pnrResponse.sourceStation)
        view.toStnTv.setText(pnrResponse.destinationStation)
        view.durationTv.setText(pnrResponse.journeyClass+" | "+pnrResponse.quota)
        view.boardingStn.setText("Boarding point - "+pnrResponse.boardingPoint+
                "\nDate Of Journey - "+pnrResponse.dateOfJourney)

        view.passengerContainerLin!!.removeAllViews()
        for(list in pnrResponse.passengerList.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_status, null)
            var sno: TextView =child.findViewById(R.id.snoTv)
            var ageTv: TextView =child.findViewById(R.id.ageTv)
            var bookingStatus: TextView =child.findViewById(R.id.bookingStatusTv)
            var currentStatus: TextView =child.findViewById(R.id.currentStatusTv)
            sno.text= list.passengerSerialNumber+"."
            ageTv.text= list.passengerAge
            bookingStatus.text= list.bookingStatus
            if(list.currentStatus.contains("CNF")){
                currentStatus.text= list.currentStatus+"\n"+list.bookingCoachId+"-"+list.bookingBerthNo
            }else{
                currentStatus.text= list.currentStatus
            }
            view.passengerContainerLin!!.addView(child)
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: PnrStatusResponse, param2: String) =
                TrainPnrStatusDetailFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}