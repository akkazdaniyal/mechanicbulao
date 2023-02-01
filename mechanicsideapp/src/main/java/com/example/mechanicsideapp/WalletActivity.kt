package com.example.mechanicsideapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.wallettransaction_row.view.*

class WalletActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    var amount: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.getReference("Mechanics")
        mAuth = FirebaseAuth.getInstance()

        val txtMoney = findViewById<View>(R.id.txtMoneyAmount) as TextView

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                amount = snapshot.child("walletMoney").getValue(Int::class.java)
                Log.d("Wallet","Money: $amount")
                if(snapshot.child("walletMoney").exists()){
                    txtMoney.setText("$amount")
                }else{
                    txtMoney.setText("0")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val backwallet = findViewById<View>(R.id.backWallet) as ImageButton
        backwallet.setOnClickListener{
            val intent = Intent(this,DashboardmechanicActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }

        val btnAddMoney = findViewById<View>(R.id.btnAddMoneyWallet) as Button
        btnAddMoney.setOnClickListener{
            addMoneyDailog()
        }

        val btnWithdrawMoney = findViewById<View>(R.id.btnWithdrawMoneyWallet) as Button
        btnWithdrawMoney.setOnClickListener{
            withdrawMoneyDailog()
        }


        listenforTransactions()

    }

    private fun listenforTransactions(){
        val currentUser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/wallet-transactions/$currentUser")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactionsView = findViewById<View>(R.id.viewTransactions) as RecyclerView
                val adapter = GroupieAdapter()
                snapshot.children.forEach{
                    val transaction = it.getValue(Transactions::class.java)
                    Log.d("Wallet","Transactions: $snapshot")
                    if(transaction != null && transaction.senderId==currentUser){
                        adapter.add(LatestTransaction(transaction))
                    }
                }
                val layoutManager = LinearLayoutManager(application)
                layoutManager.setReverseLayout(true)
                layoutManager.setStackFromEnd(true)
                transactionsView.setLayoutManager(layoutManager)
                transactionsView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    class LatestTransaction(val transac: Transactions) : Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            viewHolder.itemView.txtTransactionType.text = transac.transactionType
//            viewHolder.itemView.txtTransactionAmount.text = transac.transactionAmount.toString()
            val transactype = transac.transactionType
            val transacAmount = transac.transactionAmount.toString()

            if(transactype == "debit"){
                viewHolder.itemView.txtTransactionType.text = "Amount Debited: "
                viewHolder.itemView.txtTransactionAmount.text = "Rs. $transacAmount"
            }else if(transactype == "credit"){
                viewHolder.itemView.txtTransactionType.text = "Amount Credited: "
                viewHolder.itemView.txtTransactionAmount.text = "Rs. $transacAmount"
            }
        }
        override fun getLayout(): Int {
            return R.layout.wallettransaction_row
        }
    }


    private fun addMoneyDailog(){
        val builder =  AlertDialog.Builder (this)
        builder.setTitle("Enter the amount of money")
        val inputAmount = EditText(this)
        var oldMoneyAmount: Int = Integer.parseInt(amount.toString())

        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER)
        builder.setView(inputAmount)

        builder.setPositiveButton("Add") { dialog, which ->
            Toast.makeText(applicationContext,
                "Money Added!", Toast.LENGTH_SHORT).show()
            val inputMoney = inputAmount.text.toString()
            val currMoneyAmount = Integer.parseInt(inputMoney.toString())
            val newMoneyAmount: Int = oldMoneyAmount + currMoneyAmount

            val mUser = mAuth!!.currentUser
            val mUseruid = mAuth!!.uid
            val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
            mUserReference.child("walletMoney").setValue(newMoneyAmount).addOnCompleteListener{
                val walletTransaction = FirebaseDatabase.getInstance().getReference("/wallet-transactions/$mUseruid").push()
                val transacaction = Transactions(walletTransaction.key!!,"credit",currMoneyAmount,mUseruid!!, mUseruid!!,System.currentTimeMillis()/1000)
                walletTransaction.setValue(transacaction)

                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just added Rs. $currMoneyAmount to your wallet. Your new balance is Rs. $newMoneyAmount", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
                Toast.makeText(this, "Amount updated successfully!", Toast.LENGTH_LONG).show()
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }

        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(applicationContext,
                "Canceled adding money!", Toast.LENGTH_SHORT).show()
        }

        builder.show()
    }

    private fun withdrawMoneyDailog(){
        val builder =  AlertDialog.Builder (this)
        builder.setTitle("Enter the amount of money")
        val inputAmount = EditText(this)
        var oldMoneyAmount: Int = Integer.parseInt(amount.toString())


        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER)
        builder.setView(inputAmount)

        builder.setPositiveButton("Withdraw") { dialog, which ->
            Toast.makeText(applicationContext,
                "Money Withdrawn!", Toast.LENGTH_SHORT).show()
            val inputMoney = inputAmount.text.toString()
            val currMoneyAmount = Integer.parseInt(inputMoney.toString())
            val newMoneyAmount: Int = oldMoneyAmount - currMoneyAmount
            val mUser = mAuth!!.currentUser
            val mUseruid = mAuth!!.uid
            val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
            mUserReference.child("walletMoney").setValue(newMoneyAmount).addOnCompleteListener{
                val walletTransaction = FirebaseDatabase.getInstance().getReference("/wallet-transactions/$mUseruid").push()
                val transacaction = Transactions(walletTransaction.key!!,"debit",currMoneyAmount,mUseruid!!, mUseruid!!,System.currentTimeMillis()/1000)
                walletTransaction.setValue(transacaction)

                val latestActivity = FirebaseDatabase.getInstance().getReference("/latest-activities/$mUseruid").push()
                val activities = Activities(latestActivity.key!!,"You have just withdrawn Rs. $currMoneyAmount from your wallet. Your new balance is Rs. $newMoneyAmount", mUseruid!!,System.currentTimeMillis()/1000)
                latestActivity.setValue(activities)
                Toast.makeText(this, "Amount withdrawn successfully!", Toast.LENGTH_LONG).show()
            }.addOnFailureListener{ ex ->
                Toast.makeText(this, "Error:" +ex.message.toString(), Toast.LENGTH_LONG).show()
            }

        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(applicationContext,
                "Canceled withdrawing money!", Toast.LENGTH_SHORT).show()
        }

        builder.show()
    }
}