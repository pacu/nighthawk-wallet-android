package com.nighthawkapps.wallet.android.ui.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.nighthawkapps.wallet.android.R
import com.nighthawkapps.wallet.android.databinding.FragmentTransferBinding
import com.nighthawkapps.wallet.android.ext.gone
import com.nighthawkapps.wallet.android.ext.visible
import com.nighthawkapps.wallet.android.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransferFragment : BaseFragment<FragmentTransferBinding>() {

    private val transferViewModel: TransferViewModel by activityViewModels()

    override fun inflate(inflater: LayoutInflater): FragmentTransferBinding {
        return FragmentTransferBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                transferViewModel.currentUIScreen.collect {
                    onUIScreenUpdated(it)
                }
            }
        }

        binding.hitAreaExit.setOnClickListener { onBackPressed() }
    }

    private fun onBackPressed() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (transferViewModel.currentUIScreen.value) {
                TransferViewModel.UIScreen.RECEIVE, TransferViewModel.UIScreen.TOP_UP -> transferViewModel.updateUIScreen(TransferViewModel.UIScreen.LANDING)
                else -> { }
            }
        }
    }

    private fun onUIScreenUpdated(uiScreen: TransferViewModel.UIScreen) {
        when (uiScreen) {
            TransferViewModel.UIScreen.LANDING -> showLandingUIScreen()
            TransferViewModel.UIScreen.RECEIVE -> showReceiveUIScreen()
            TransferViewModel.UIScreen.TOP_UP -> showTopUpUIScreen()
        }
    }

    private fun showLandingUIScreen() {
        with(binding) {
            groupReceive.gone()
            groupTopUp.gone()

            updateTitleAndBackButton(getString(R.string.ns_send_and_receive_zcash), false)

            viewSendMoney.updateTransferItemsData(R.drawable.ic_arrow_back_black_24dp, getString(R.string.ns_send_money), getString(R.string.ns_send_money), 180f) {}

            viewReceiveMoney.updateTransferItemsData(R.drawable.ic_arrow_back_black_24dp, getString(R.string.ns_receive_money), getString(R.string.ns_receive_money_publicly_text)) {
                transferViewModel.updateUIScreen(TransferViewModel.UIScreen.RECEIVE)
            }

            viewTopUpWallet.updateTransferItemsData(R.drawable.ic_icon_top_up, getString(R.string.ns_top_up_wallet), getString(R.string.ns_top_up_text)) {
                transferViewModel.updateUIScreen(TransferViewModel.UIScreen.TOP_UP)
            }

            groupLanding.visible()
        }
    }

    private fun showReceiveUIScreen() {
        with(binding) {
            groupLanding.gone()
            groupTopUp.gone()

            updateTitleAndBackButton(getString(R.string.ns_receive_money_securely), true)

            viewShowQRCode.updateTransferItemsData(R.drawable.ic_icon_scan_qr, getString(R.string.ns_show_qr_code), getString(R.string.ns_show_qr_code_text)) {}

            viewCopyPrivateAddress.updateTransferItemsData(R.drawable.ic_content_copy, getString(R.string.ns_copy_private_address), getString(R.string.ns_copy_private_address_text)) {}

            viewCopyNonPrivateAddress.updateTransferItemsData(R.drawable.ic_icon_transparent, getString(R.string.ns_copy_public_address), getString(R.string.ns_receive_money_publicly_text)) {}

            groupReceive.visible()
        }
    }

    private fun showTopUpUIScreen() {
        with(binding) {
            groupLanding.gone()
            groupReceive.gone()

            updateTitleAndBackButton(getString(R.string.ns_top_up), true)

            viewMoonPay.updateTransferItemsData(R.drawable.ic_icon_moon_pay, getString(R.string.ns_buy_moonpay), getString(R.string.ns_buy_moonpay_text)) {}

            viewSideShift.updateTransferItemsData(R.drawable.ic_icon_side_shift, getString(R.string.ns_swap_sideshift), getString(R.string.ns_swap_sideshift_text)) {}

            viewStealthEx.updateTransferItemsData(R.drawable.ic_icon_side_shift, getString(R.string.ns_swap_stealthex), getString(R.string.ns_swap_stealthex_text)) {}

            groupTopUp.visible()
        }
    }

    private fun updateTitleAndBackButton(title: String, showBackButton: Boolean) {
        binding.tvTitle.text = title
        binding.ivBack.isInvisible = showBackButton.not()
    }

    private fun ViewBinding.updateTransferItemsData(@DrawableRes icon: Int, title: String, subTitle: String, iconRotationAngle: Float = 0f, onRootClick: () -> Unit) {
        with(this.root) {
            findViewById<AppCompatImageView>(R.id.ivLeftIcon).apply {
                setImageResource(icon)
                if (iconRotationAngle != 0F) {
                    rotation = iconRotationAngle
                }
            }
            findViewById<TextView>(R.id.tvItemTitle).text = title
            findViewById<TextView>(R.id.tvItemSubTitle).text = subTitle
            setOnClickListener { onRootClick.invoke() }
        }
    }
}
