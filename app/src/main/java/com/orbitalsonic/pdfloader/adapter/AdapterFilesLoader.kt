package com.orbitalsonic.pdfloader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orbitalsonic.pdfloader.R
import com.orbitalsonic.pdfloader.databinding.ItemFilesBinding
import com.orbitalsonic.pdfloader.datamodel.FileItem
import com.orbitalsonic.pdfloader.interfaces.OnItemClickListener

class AdapterFilesLoader : ListAdapter<FileItem, RecyclerView.ViewHolder>(DATA_COMPARATOR){

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemFilesBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.item_files,parent,false)
        viewHolder = GalleryLoaderViewHolder(binding, mListener!!)
        return viewHolder

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        val viewHolder = holder as GalleryLoaderViewHolder
        viewHolder.bind(currentItem)

    }


    class GalleryLoaderViewHolder(binding:ItemFilesBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        private val mBinding = binding
        init {

            binding.item.setOnClickListener {
                val mPosition = adapterPosition
                listener.onItemClick(mPosition)
            }

        }

        fun bind(mCurrentItem: FileItem) {
            Glide.with(mBinding.root.context)
                .load(R.drawable.icon_pdf)
                .placeholder(R.drawable.bg_glide)
                .centerCrop()
                .into(mBinding.fileImage)

            mBinding.fileName.text = mCurrentItem.pdfFilePath.name
        }

    }

    companion object {
        private val DATA_COMPARATOR = object : DiffUtil.ItemCallback<FileItem>() {
            override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
                return oldItem.pdfFilePath == newItem.pdfFilePath
            }
        }
    }

}