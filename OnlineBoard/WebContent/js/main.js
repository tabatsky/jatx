$(document).ready(function(){
	window.reloadSettings = function(){
		if (localStorage) {
			window.blackboardBgColor = localStorage.getItem('blackboardBgColor');
			window.blackboardTextColor = localStorage.getItem('blackboardTextColor');
			window.msgBgColor = localStorage.getItem('msgBgColor');
			window.msgTextColor = localStorage.getItem('msgTextColor');
			window.blackBoardInterval = localStorage.getItem('blackBoardInterval');
			window.msgInterval = localStorage.getItem('msgInterval');
			window.msgCount = localStorage.getItem('msgCount');
			window.sounds = localStorage.getItem('sounds');
		}
		
		if (!window.blackboardBgColor) {
			window.blackboardBgColor = '#003700';
		}
		if (!window.blackboardTextColor) {
			window.blackboardTextColor = '#ffffff';
		}
		if (!window.msgBgColor) {
			window.msgBgColor = '#ffffff';
		}
		if (!window.msgTextColor) {
			window.msgTextColor = '#000000';
		}
		if (!window.blackBoardInterval) {
			window.blackBoardInterval = 5;
		}
		if (!window.msgInterval) {
			window.msgInterval = 2;
		}
		if (!window.msgCount) {
			window.msgCount = 30;
		}
		
		if (!window.sounds) {
			window.sounds = 'yes';
		} 
		
		$('#img1,#img2').css('background',window.blackboardBgColor);
		$('#img1,#img2').css('color',window.blackboardTextColor);
		$('#msgList').css('background',window.msgBgColor);
		$('#msgList').css('color',window.msgTextColor);
		$('#msgBoard,#blackBoard1,#filename').css('background',window.msgTextColor);
		$('#filename').css('color',window.msgBgColor);
		$('#send,#choose_file,#subm,#open_settings').css('background',window.msgBgColor);
		$('#send,#choose_file,#subm,#open_settings').css('color',window.msgTextColor);
		$('#send,#choose_file,#subm,#open_settings').css('border-color',window.msgTextColor);
		$('textarea,#blackBoard2').css('background',window.msgBgColor);
		$('textarea,#blackBoard2').css('color',window.msgTextColor);
		
		window.restartUpdaters();
		
		$('#blackboardBgColor').val(window.blackboardBgColor);
		$('#blackboardTextColor').val(window.blackboardTextColor);
		$('#msgBgColor').val(window.msgBgColor);
		$('#msgTextColor').val(window.msgTextColor);
		$('#blackBoardInterval').val(window.blackBoardInterval);
		$('#msgInterval').val(window.msgInterval);
		$('#msgCount').val(window.msgCount);
		$('#sounds').prop('checked',(window.sounds=='yes'));
	};
	
	window.saveSettings = function() {
		window.blackboardBgColor = $('#blackboardBgColor').val();
		window.blackboardTextColor = $('#blackboardTextColor').val();
		window.msgBgColor = $('#msgBgColor').val();
		window.msgTextColor = $('#msgTextColor').val();
		window.blackBoardInterval = $('#blackBoardInterval').val();
		window.msgInterval = $('#msgInterval').val();
		window.msgCount = $('#msgCount').val();
		window.sounds = $('#sounds').prop('checked')?'yes':'no';
		
		if (localStorage) {
			localStorage.setItem('blackboardBgColor',window.blackboardBgColor);
			localStorage.setItem('blackboardTextColor',window.blackboardTextColor);
			localStorage.setItem('msgBgColor',window.msgBgColor);
			localStorage.setItem('msgTextColor',window.msgTextColor);
			localStorage.setItem('blackBoardInterval',window.blackBoardInterval);
			localStorage.setItem('msgInterval',window.msgInterval);
			localStorage.setItem('msgCount',window.msgCount);
			localStorage.setItem('sounds',window.sounds);
		}
	};
	
	
	window.updateMsgBoard = function(){
		if (!(window.user_id&&window.password&&window.room)) return;
		var uri = 'printmsg?user_id=' + window.user_id
						+ '&password=' + window.password
						+ '&room=' + window.room
						+ '&count=' + window.msgCount;
		$.get(uri, function(data){
			if (!window.msgBackup) {
				window.msgBackup = data;
				$('#msgList').html(data);
			} else if (window.msgBackup!=data) {
				$('#msgList').html(data);
				window.msgBackup = data;
				if (window.sounds=='yes') ion.sound.play('button_click_on');
			}
		});
	};
	
	window.updateBlackBoard = function(){
		if (!(window.user_id&&window.password&&window.room)) return;
		var uri = 'printboard?user_id=' + window.user_id
						+ '&password=' + window.password
						+ '&room=' + window.room;
		$.get(uri, function(data){
			if (!window.blackBoardBackup) {
				window.blackBoardBackup = data;
				$('#blackBoard2 pre').text(data);
				$('#img2 p').text('$jaxStart$'+data+'$jaxEnd$');
				MathJax.Hub.Queue(["Typeset",MathJax.Hub,"img2"]);
			} else if (window.blackBoardBackup!=data) {
				$('#blackBoard2 pre').text(data);
				$('#img2 p').text('$jaxStart$'+data+'$jaxEnd$');
				MathJax.Hub.Queue(["Typeset",MathJax.Hub,"img2"]);
				window.blackBoardBackup = data;
				if (window.sounds=='yes') ion.sound.play('bell_ring');
			}
		});
	};
	
	window.updateMyBoard = function(){
		if (!(window.user_id&&window.password&&window.room)) return;
		var uri = 'printboard?user_id=' + window.user_id
						+ '&password=' + window.password
						+ '&room=' + window.room
						+ '&my=1';
		$.get(uri, function(data){
			$('#myBoard').val(data);
			$('#show').trigger('click');
		});
	};
	
	window.restartUpdaters = function() {
		clearInterval(window.msgUpdater);
		clearInterval(window.blackBoardUpdater);
		
		window.msgUpdater = setInterval(function(){window.updateMsgBoard()}, 1000*window.msgInterval);
		window.blackBoardUpdater = setInterval(window.updateBlackBoard, 1000*window.blackBoardInterval);
	};
	
	ion.sound({
	    sounds: [
	        {
	            name: 'button_click_on'
	        },
	        {
	            name: 'button_click'
	        },
	        {
	            name: 'bell_ring'
	        }
	    ],
	    volume: 0.5,
	    path: "sounds/",
	    preload: true
	});
	
	window.reloadSettings();
		
	window.updateMsgBoard(0);
	window.updateBlackBoard();
	window.updateMyBoard();
	
	window.restartUpdaters();
	
	$('#choose_file').click(function(){
		$('#my_file').trigger('click');
	});
	
	$('#my_file').on('change',function() {
		var value = $('#my_file')[0].files[0].name;
		$('#filename').val(value);
	});
	
	$('#send').click(function(){
		if (window.sounds=='yes') ion.sound.play('button_click');
		
		if (!(window.user_id&&window.password&&window.room)) return;
		var msg = $('#msg').val();
		
		var files = $('#my_file')[0].files;
		
		if (!files[0]) {	
			$.post('sendmsg',{
				user_id: window.user_id,
				password: window.password,
				room: window.room,
				msg: msg
			},function(){
		        window.updateMsgBoard();
			});
		
			$('#msg').val('');
		} else {
			$('#send').prop('disabled',true);
			
			var fd = new FormData();
			fd.append('file', files[0]);
			fd.append('user_id', window.user_id);
			fd.append('password', window.password);
			fd.append('room', window.room);
			fd.append('msg', msg);
			fd.append('action', 'upload');
			fd.append('file_name',files[0].name);
			
			$.ajax({
				url: 'fileupload',
			    data: fd,
			    async: true,
			    cache: false,
			    contentType: false,
			    processData: false,
			    type: 'POST',
			    success: function(data){
			    	if (data.lastIndexOf('error',0) === 0) {
			    		alert(data);
			    	}
			        window.updateMsgBoard();
			        $('#send').prop('disabled',false);
			    }
		    });
			
			$('#msg').val('');
			$('#my_file')[0].value='';
			$('#filename').val('');
		}
	});
	
	$('#msg').keydown(function (e) {
		if (e.ctrlKey && e.keyCode == 13) {
		    $('#send').trigger('click');
		}
	});
	
	$('#subm').click(function(){
		$('#subm').prop('disabled',true);
		
		if (!(window.user_id&&window.password&&window.room)) return;
		var boardtext = $('#myBoard').val();
		
		$.post('submitboard', {
			user_id: window.user_id,
			password: window.password,
			room: window.room,
			boardtext: boardtext
		}, function(data){
			$('#subm').prop('disabled',false);
			$('#show').trigger('click');
		});
	});
	
	$('#show').click(function() {
		var boardtext = $('#myBoard').val();
		$('#img1 p').text('$jaxStart$'+boardtext+'$jaxEnd$');
		MathJax.Hub.Queue(["Typeset",MathJax.Hub,"img1"]);
	});
	
	$('#myBoard').keyup(function(){
		$('#show').trigger('click');
	});
	
	$('#open_settings').click(function(){
		$('#settings').show();
	});
	
	$('#cancel').click(function(){
		$('#settings').hide();
	});
	
	$('#save').click(function(){
		window.saveSettings();
		window.reloadSettings();
		$('#settings').hide();
	});
	
	$('#reset').click(function(){
		if (confirm('Reset cannot be undone. Proceed?')) {
			if (localStorage) {
				localStorage.clear();
			}
			window.reloadSettings();
		}
	});
});