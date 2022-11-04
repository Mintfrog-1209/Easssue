import React, { useEffect, useState } from "react"
import { SettingModalProps } from "./types"
import { Button } from "flowbite-react"
export const SettingModal: React.FC<SettingModalProps> = ({setSettingModalOpen}) => {
  const [bgimg, setBgimg] = useState("")
  const onCloseClick = () => {
    setSettingModalOpen(false)
  }
  const onSaveClick = () => {
    chrome.storage.local.set({'bgimg': bgimg}, ()=> {
    } )
    setSettingModalOpen(false)
  }
  const handleClickInput = (event:any) => {
    const fr = new FileReader();
    const file = event.target.files[0];
    fr.readAsDataURL(file)
    fr.onload = (event:any) => {
      const url = event.target.result
      setBgimg(url)
    }
  }
  
  return (
    <>
      <div className="absolute top-0 left-0 w-screen h-screen bg-black/75">
        <div className="p-2 z-50 h-1/2 w-1/2 absolute top-1/4 left-1/4 bg-white rounded-lg">
          <div className="text-xl">
            설정
            <button className="absolute right-2" onClick={onCloseClick}>
              X
            </button>
          </div>
          <input type="file" onChange={handleClickInput} />
          <Button onClick={onSaveClick}>저장</Button>
        </div>
      </div>
    </>
  )
}